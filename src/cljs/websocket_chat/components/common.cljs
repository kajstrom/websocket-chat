(ns websocket-chat.components.common
  (:require [reagent.core :as r]))

(defn modal [header body footer]
  [:div.modal.fade.show {:role "document" :style {:display :block}}
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:h3.modal-title header]]
     [:div.modal-body body]
     [:div.modal-footer footer]]]])