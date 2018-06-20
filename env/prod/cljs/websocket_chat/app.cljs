(ns websocket-chat.app
  (:require [websocket-chat.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
