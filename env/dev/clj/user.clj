(ns user
  (:require [websocket-chat.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [websocket-chat.figwheel :refer [start-fw stop-fw cljs]]
            [websocket-chat.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'websocket-chat.core/repl-server))

(defn stop []
  (mount/stop-except #'websocket-chat.core/repl-server))

(defn restart []
  (stop)
  (start))


