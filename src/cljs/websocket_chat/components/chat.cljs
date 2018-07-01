(ns websocket-chat.components.chat
  (:require [reagent.core :refer [atom]]
            [websocket-chat.components.common :as c]
            [websocket-chat.ws :refer [join-chat!]]
            [cljs-time.core :as ct]
            [cljs-time.format :as cf]))

(defn message-area [messages]
    (let [messages @messages]
      [:ul.list-group.list-group-flush
       (for [message messages]
         ^{:key (:id message)} [:li.list-group-item
                                (cf/unparse (:date-hour-minute-second cf/formatters) (:time message)) " :: " (:user message) " - " (:message message)])]))

(defn send-message! [messages new-message]
  (swap! messages conj {:id (count @messages) :user "Kaj" :message new-message :time (ct/now)}))

(defn message-form [messages]
  (let [fields (atom {:message ""})]
    (fn []
      [:div.col-12
       [:div.form-group
        [:textarea.form-control {:rows 2 :value (:message @fields) :on-change #(swap! fields assoc :message (-> % .-target .-value))}]
        [:button.btn.btn-primary {:on-click #(do
                                              (send-message! messages (:message @fields))
                                              (reset! fields {:message ""}))} "Send"]]])))

(defn participant-list [participants]
  (let [participants @participants]
    [:div [:h4 "Participants"]
     [:ul.list-group.list-group-flush
      (for [participant participants]
        ^{:key (:id participant)} [:li.list-group-item
                                   (:name participant)])]]))

(defn signup-form [session participants]
  (let [fields (atom {})]
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
                               }]]]
       [:div
        [:button.btn.btn-primary {:on-click (fn [] (do
                                                     (swap! session dissoc :modal)
                                                     (join-chat! (:name @fields))))} "Enter Chat"]]])))