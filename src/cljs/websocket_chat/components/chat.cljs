(ns websocket-chat.components.chat
  (:require [reagent.core :refer [atom]]))

(defn message-form []
  (let [fields (atom {:message ""})]
    (fn []
      [:div.col-12
       [:div.form-group
        [:textarea.form-control {:rows 2 :value (:message @fields) :on-change #(swap! fields assoc :message (-> % .-target .-value))}]
        [:button.btn.btn-primary {:on-click #(println (:message @fields))} "Send"]]])))