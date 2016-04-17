(ns geostitcher.routes.home
  (:require [compojure.core :refer :all]
            [noir.session :as session]
            [geostitcher.views.layout :as layout]))

(defn home []
  (layout/common [:h1 "Hello " (session/get :username)]))

(defroutes home-routes
  (GET "/" [] (home)))
