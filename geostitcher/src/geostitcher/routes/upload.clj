(ns geostitcher.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [geostitcher.views.layout :as layout]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [geostitcher.models.db :as db]
            ;;[geostitcher.util :refer [galleries gallery-path]]
            )
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))
(defn gallery-path []
  "galleries")

(defn upload-page [info]
  (layout/common
    [:h2 "Upload an image set"]
    [:p info]
    (form-to {:enctype "multipart/form-data"}
             [:post "/upload"]
             (file-upload :file)
             (submit-button "upload"))))

(defn handle-upload [{:keys [filename] :as file}]
  (println file)
  (upload-page 
    (if (empty? filename)
    "Please select a file to upload"
    "Success")))

(defroutes upload-routes
  (GET "/upload" [info] (upload-page info))
  (POST "/upload" [file] (handle-upload file)))
