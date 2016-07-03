(ns geostitcher.util
  (:require [noir.session :as session]
            [hiccup.util :refer [url-encode]])
  (:import java.io.File))

(def thumb-size
  150)

(def thumb-prefix "thumb_")

(def galleries "galleries")

(defn gallery-path []
  (str galleries File/separator (session/get :username)))

(defn image-uri [userid dataset_id file-name]
  (str "/img/" userid "/" dataset_id "/" (url-encode file-name)))

(defn thumb-uri [userid dataset_id file-name]
  (println (image-uri userid dataset_id (str thumb-prefix file-name)))
  (image-uri userid dataset_id (str thumb-prefix file-name)))

