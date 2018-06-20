(ns websocket-chat.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[websocket-chat started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[websocket-chat has shut down successfully]=-"))
   :middleware identity})
