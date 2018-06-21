(ns websocket-chat.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [websocket-chat.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary :include-macros true]
            [websocket-chat.components.chat :refer [message-form message-area]]
            [cljs-time.core :as ct])
  (:import goog.History))

(defonce session (r/atom {:page :chat}))
(defonce messages (r/atom [{:id 0 :user "Kaj" :message "Hello world!" :time (ct/now)}]))

(defn chat-page []
  [:div.container-fluid
   [:div.row.chat-area
    [:div.col-10 [message-area messages]]
    [:div.col-2 "Participants"]]
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
