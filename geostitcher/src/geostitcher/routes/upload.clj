(ns geostitcher.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [geostitcher.views.layout :as layout]
            [geostitcher.util :refer  [galleries gallery-path thumb-prefix thumb-uri thumb-size]]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [taoensso.timbre :refer [error]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [geostitcher.models.db :as db])
  
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance (double ratio) (double ratio))
        transform-op (AffineTransformOp. scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let 
    [img (ImageIO/read file)
     img-width (.getWidth img)
     img-height (.getHeight img)
     ratio (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
      (scale-image (io/input-stream (str path filename)))
      "jpeg"
      (File. (str path thumb-prefix filename)))))

(defn serve-file [user-id file-name]
  (file-response (str galleries File/separator user-id File/separator file-name)))

(defn upload-page [params]
  (layout/render "upload.html"
                 params))

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page 
    (if (empty? filename)
    {:error "Please select file to upload"}
    (try
      (noir.io/upload-file (gallery-path) file :create-path? true)
      (save-thumbnail file)
      (db/add-image (session/get :username) filename)
      {:image (thumb-uri (session/get :username) filename)}
      (catch Exception ex
        (error ex "An error has occured while uploading" name)
        {:error (str "Error uploading file" (.getMessage ex))})))))

(defn delete-image [userid name]
  (try
    (db/delete-image userid name)
    (io/delete-file (str (gallery-path) File/separator name ))
    (io/delete-file (str (gallery-path) File/separator thumb-prefix name ))
    "ok"
    (catch Exception ex (.getMessage ex))))

(defn delete-images [names]
  (let [userid (session/get :username)]
    (resp/json
      (for [name names] {:name name :status (delete-image userid name)}))))

(defroutes upload-routes
  (GET "/upload" [info] (restricted (upload-page {:info info})))
  (POST "/upload" [file] (restricted (handle-upload file)))
  (GET "/img/:username/:file-name" [username file-name] (serve-file username file-name))
  (POST "/delete" [names] (restricted (delete-images names))))
