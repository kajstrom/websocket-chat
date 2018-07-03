(ns websocket-chat.routes.ws
  (:require [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.immutant :refer (get-sch-adapter)]
            [compojure.core :refer [defroutes GET POST]]
            [mount.core :refer [defstate]]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(defonce participants (atom []))

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (get-sch-adapter) {:user-id-fn (fn [ring-req]
                                                                   (get-in ring-req [:params :client-id]))})]

  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))

(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= new old)
               (if-let [disconnected (second (clojure.data/diff (:any new) (:any old)))]
                 (doseq [uid disconnected]
                   (swap! participants (fn [participants] (filter #(not= uid (:id %)) participants)))
                   (doseq [uid (:any @connected-uids)]
                     (chsk-send! uid [:chat/participants-updated @participants])))))))

(defn handle-message! [{:keys [id client-id ?data]}]
  (when (= id :chat/join)
    (let [participant (assoc ?data :id client-id :name (:name ?data))]
      (swap! participants conj participant))
    (println @participants)
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid [:chat/participants-updated @participants]))
    true)
  (when (= id :chat/message)
    (let [message (assoc ?data
                         :id (.toString (java.util.UUID/randomUUID))
                         :time (f/unparse (f/formatter :basic-date-time) (t/now))
                         :user (:name (first (filter #(= client-id (:id %)) @participants))))]
      (println message)
      (doseq [uid (:any @connected-uids)]
        (chsk-send! uid [:chat/new-message message])))
    true))

(defn stop-router! [stop-fn]
  (when stop-fn (stop-fn)))

(defn start-router! []
  (sente/start-chsk-router! ch-chsk handle-message!))

(defstate router
  :start (start-router!)
  :stop (stop-router! router))

(defroutes ws-routes
  (GET "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post req)))