(ns geostitcher.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [noir.util.middleware :as noir-middleware]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [geostitcher.routes.home :refer [home-routes]]))

(defn init []
  (println "geostitcher is starting"))

(defn destroy []
  (println "geostitcher is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler [home-routes app-routes]))
