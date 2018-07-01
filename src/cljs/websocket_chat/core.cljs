(ns websocket-chat.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [websocket-chat.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary :include-macros true]
            [websocket-chat.components.chat :refer [message-form message-area participant-list signup-form]]
            [websocket-chat.ws :refer [start-router!]]
            [cljs-time.core :as ct])
  (:import goog.History))

(defonce session (r/atom {:page :chat}))
(defonce messages (r/atom []))
(defonce participants (r/atom []))

(defn participants-updated [new-participants]
  (reset! participants new-participants))

(defn modal[]
  (when-let [session-modal (:modal @session)]
    [session-modal]))

(defn chat-page []
  (swap! session assoc :modal (signup-form session participants))
<<<<<<< HEAD
  (start-router! {:chat/participants-updated participants-updated})
=======
  (start-router!)
>>>>>>> 87a201185c5c33e69a0df9dfaddbd03c06794ff1
  [:div.container-fluid
   [modal]
   [:div.row.chat-area
    [:div.col-10 [message-area messages]]
    [:div.col-2 [participant-list participants]]]
   [:div.row.typing-area
    [message-form messages]]])

(def pages
  {:chat #'chat-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (swap! session assoc :page :chat))

(secretary/defroute "/about" []
  (swap! session assoc :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
            (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app

(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-components))
