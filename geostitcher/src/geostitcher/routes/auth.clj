(ns geostitcher.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [geostitcher.routes.home :refer :all]
            [geostitcher.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]))


(defroutes auth-routes
  (GET "/register" []
       (registration-page))
  
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1)))
