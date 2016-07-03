(ns geostitcher.routes.datasets
  (:require [compojure.core :refer [defroutes GET POST]]
            [geostitcher.views.layout :as layout]
            [geostitcher.util :refer [thumb-prefix]]
            [geostitcher.models.db :as db]
            [noir.session :as session]
            [noir.response :as resp]
            [geostitcher.util :refer [gallery-path]])
  (:import java.io.File))

(defn create-dataset-filepath [dataset_id]
  (let [dataset-path (File. (str (gallery-path) File/separator dataset_id ))]
    (if-not (.exists dataset-path) (.mkdirs dataset-path))
      (str (.getAbsolutePath dataset-path) File/separator)))

(defn display-datasets []
  (layout/render "datasets_list.html"
                 {:datasets     (db/get-datasets (session/get :user_id))}))

(defn display_dataset [dataset_id]
  (println (db/images-from-dataset dataset_id))
  (layout/render "dataset.html"
                 {:images     (db/images-from-dataset dataset_id)}))

(defn handle-dataset [request]
  (let [form-params (:params request)
        name (:name form-params)
        height (Integer/parseInt (:height form-params))
        camera (:camera form-params)
        user_id (session/get :user_id)]
    (try
      (let [dataset_id (db/create-dataset {:name name :height height :camera camera :user_id user_id })]
        (create-dataset-filepath dataset_id)
      (resp/redirect (str "/datasets/" dataset_id "/gallery/" user_id)))
      (catch Exception ex 
        (println ex)
        (resp/redirect "/")))))

(defroutes datasets-routes
  (GET "/datasets" []
       (display-datasets))
  (GET "/datasets/:dataset_id" [dataset_id]
       (display_dataset dataset_id))
  (POST "/datasets" request
       (handle-dataset request)))