(ns geostitcher.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [geostitcher.views.layout :as layout]
            [geostitcher.util :refer  [galleries gallery-path thumb-prefix thumb-uri thumb-size]]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [taoensso.timbre :refer [error]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [geostitcher.models.db :as db]
            [geostitcher.routes.datasets :refer [create-dataset-filepath]])
  
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

(defn save-thumbnail [{:keys [filename]} dataset_id]
  (let [path (str (create-dataset-filepath dataset_id) File/separator)]
    (ImageIO/write
      (scale-image (io/input-stream (str path filename)))
      "jpeg"
      (File. (str path thumb-prefix filename)))))

(defn serve-file [user-id dataset_id file-name]
  (file-response (str galleries File/separator user-id File/separator dataset_id File/separator file-name)))

(defn upload-page [params]
  (layout/render "upload.html"
                 params))

(defn handle-upload [{:keys [filename] :as file} dataset_id]
  (println "FILENAME" filename)
  (println "DATASET ID" dataset_id)
  (upload-page 
    (if (empty? filename)
    {:error "Please select file to upload"}
    (try
      (noir.io/upload-file (create-dataset-filepath dataset_id) file :create-path? true)
      (save-thumbnail file dataset_id)
      (println "USER ID PARSE")
      (println (session/get :user_id))

      (println "USER ID PARSED")
      (db/add-image (session/get :user_id) (java.lang.Integer/parseInt dataset_id) filename)
      {:image (thumb-uri (session/get :username) dataset_id filename)}
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
  (POST "/upload/:dataset_id" [file dataset_id] (restricted (handle-upload file dataset_id)))
  (GET "/img/:username/:dataset_id/:file-name" [username dataset_id file-name] (serve-file username dataset_id file-name))
  (POST "/delete" [names] (restricted (delete-images names))))
