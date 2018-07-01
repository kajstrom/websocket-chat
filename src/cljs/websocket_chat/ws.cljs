(ns websocket-chat.ws
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [cljs.core.async :as async :refer [<! >! put! chan]]
            [taoensso.sente :as sente :refer [cb-success?]]
            [websocket-chat.core :refer [participants]]))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk"
                                  {:type :auto})]
  (def chsk chsk)
  (def ch-chsk ch-recv) ;Receive channel
  (def chsk-send! send-fn)
  (def chsk-state state))

(defn participants-updated [new-participants]
  (reset! participants new-participants))

(defn state-handler [{:keys [?data]}]
  (.log js/console (str "state changed: " ?data)))

(defn handshake-handler [{:keys [?data]}]
  (.log js/console (str "connection established: " ?data)))

(defn default-event-handler [ev-msg]
  (.log js/console (str "Unhandled event: " (:event ev-msg))))

(defn chat-event-handler [ev-msg]
  (let [[id data] (second (:event ev-msg))]
    (case id
      :chat/participants-updated (participants-updated data)
      (.log js/console id data))))

(defn event-msg-handler [& [{:keys [state handshake]
                             :or {state state-handler
                                  handshake handshake-handler}}]]
  (fn [ev-msg]
    (case (:id ev-msg)
      :chsk/handshake (handshake ev-msg)
      :chsk/state (state ev-msg)
      :chsk/recv (chat-event-handler ev-msg)
      (default-event-handler ev-msg))))

(def router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router] (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router (sente/start-chsk-router!
                   ch-chsk
                  (event-msg-handler
                   {:state state-handler
                    :handshake handshake-handler}))))

(defn join-chat! [name]
  (chsk-send! [:chat/join {:name name}] 8000
              (fn [reply]
                (println reply))))