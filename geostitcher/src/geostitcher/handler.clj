(ns geostitcher.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.defaults :refer :all]
            [noir.util.middleware :as noir-middleware]
            [noir.session         :as session]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [geostitcher.routes.home :refer [home-routes]]
            [geostitcher.routes.auth :refer [auth-routes]]
            [geostitcher.routes.upload :refer [upload-routes]]
            [geostitcher.routes.datasets :refer [datasets-routes]]
            [geostitcher.routes.gallery :refer [gallery-routes]]))

(defn user-page [_]
  (session/get :username))

(defn init []
  (println "geostitcher is starting"))

(defn destroy []
  (println "geostitcher is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler [home-routes auth-routes upload-routes datasets-routes gallery-routes app-routes ]
                                      :access-rules [user-page]
                                      :ring-defaults (assoc site-defaults :security {:anti-forgery false})))
