(ns websocket-chat.handler
  (:require 
            [websocket-chat.layout :refer [error-page]]
            [websocket-chat.routes.home :refer [home-routes]]
            [websocket-chat.routes.ws :refer [ws-routes]]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [websocket-chat.env :refer [defaults]]
            [mount.core :as mount]
            [websocket-chat.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))
          (-> ws-routes
              ring.middleware.keyword-params/wrap-keyword-params
              ring.middleware.params/wrap-params)
          (route/not-found
             (:body
               (error-page {:status 404
                            :title "page not found"}))))))

