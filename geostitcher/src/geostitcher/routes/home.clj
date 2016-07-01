(ns geostitcher.routes.home
  (:require [compojure.core :refer :all]
            [noir.session :as session]
            [geostitcher.views.layout :as layout]
            [geostitcher.util :refer [thumb-prefix]]
            [geostitcher.models.db :as db ]))

(defn home []
  (layout/render "home.html"
                 {:thumb-prefix thumb-prefix
                  :galleries (db/get-gallery-previews)}))

(defroutes home-routes
  (GET "/" [] (home)))
