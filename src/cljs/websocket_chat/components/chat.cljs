(ns websocket-chat.components.chat
  (:require [reagent.core :refer [atom create-class]]
            [websocket-chat.components.common :as c]
            [websocket-chat.ws :refer [join-chat! send-message!]]
            [cljs-time.core :as ct]
            [cljs-time.format :as cf]))

(defn errors-component [errors id]
  (.log js/console @errors id)
  (when-let [error (id @errors)]
    [:div.alert.alert-danger error]))

(defn format-time [time]
  "Converts UTC time to local time"
  (->> (.getTimezoneOffset (js/Date.))
         (-)
      (ct/minutes)
      (ct/plus time)
      (cf/unparse (:date-hour-minute-second cf/formatters))))

(defn message-area-render [messages]
    (let [messages @messages]
      [:ul.list-group.list-group-flush
       (for [message messages]
         ^{:key (:id message)} [:li.list-group-item
                                (format-time (:time message)) " :: " (:user message) " - " (:message message)])]))

(defn scroll-to-last-message []
  (let [el (.querySelector js/document ".chat-area-messages")]
    (set! (.-scrollTop el) (.-scrollHeight el))))

(defn message-area [messages]
  (create-class {:reagent-render #(message-area-render messages) :component-did-update scroll-to-last-message}))

(defn message-form [messages]
  (let [fields (atom {:message ""})]
    (fn []
      [:div.col-12
       [:div.form-group
        [:textarea.form-control {:rows 2 :value (:message @fields) :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
       [:button.btn.btn-primary {:on-click #(do
                                             (send-message! {:message (:message @fields)})
                                             (reset! fields {:message ""}))} "Send"]])))

(defn participant-list [participants]
  (let [participants @participants]
    [:div [:h4 "Participants"]
     [:ul.list-group.list-group-flush
      (for [participant participants]
        ^{:key (:id participant)} [:li.list-group-item
                                   (:name participant)])]]))

(defn signup-form [session participants]
  (let [fields (atom {})
        errors (atom nil)]
    (fn []
      [c/modal
       [:div "Enter username"]
       [:div
        [:div.form-group
         [:label "Name"]
         [:input.form-control {:name "name"
                               :type "text"
                               :on-change #(swap! fields assoc :name (-> % .-target .-value))
                               :placeholder "Name visible for other users"
                               }]
         [errors-component errors :name]]]
       [:div
        [:button.btn.btn-primary {:on-click (fn [] (do
                                                     (join-chat! (:name @fields) errors session)))} "Enter Chat"]]])))