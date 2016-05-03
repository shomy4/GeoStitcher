(ns geostitcher.routes.home
  (:require [compojure.core :refer :all]
            [noir.session :as session]
            [geostitcher.views.layout :as layout]
            [geostitcher.routes.gallery :refer [show-galleries]]))

(defn home []
  (layout/common (show-galleries)))

(defroutes home-routes
  (GET "/" [] (home)))
