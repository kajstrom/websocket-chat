(ns websocket-chat.components.chat
  (:require [reagent.core :refer [atom]]
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