(ns websocket-chat.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [websocket-chat.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[websocket-chat started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[websocket-chat has shut down successfully]=-"))
   :middleware wrap-dev})
