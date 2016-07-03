(ns geostitcher.routes.gallery
  (:require [compojure.core :refer [defroutes GET]]
            [geostitcher.views.layout :as layout]
            [geostitcher.util :refer [thumb-prefix]]
            [geostitcher.models.db :as db]
            [noir.session :as session]))

(defn display-gallery [dataset_id userid]
  (layout/render "gallery.html"
                 {:thumb-prefix thumb-prefix
                  :page-owner   userid
                  :dataset_id dataset_id
                  :pictures     (db/images-from-dataset (java.lang.Integer/parseInt dataset_id)  )}))

(defroutes gallery-routes
  (GET "/datasets/:dataset_id/gallery/:userid" [dataset_id userid]
       (display-gallery dataset_id userid)))