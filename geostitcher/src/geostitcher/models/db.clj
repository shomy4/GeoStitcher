(ns geostitcher.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/geostitcher"
         :user "postgres"
         :password ""})

(defn create-user [user]
    (sql/insert! db :auth_users user))

(defn get-user [username]
  (first
  (sql/query db
    ["select * from auth_users where username = ?" username] )))

(defn add-image [userid dataset_id name]
  (println userid "|" dataset_id "|" name)
  (if-not (first (sql/query db
                            ["SELECT user_id from surveys_images where user_id = ? and name = ? and dataset_id = ?" userid name dataset_id]))
    (sql/insert! db :surveys_images {:user_id userid :name name :dataset_id dataset_id})
  (throw (Exception. "You have already uploaded an image with the exact same name in same dataset"))))

(defn images-by-user [dataset_id userid]
  (println "images-by-user" dataset_id "/" userid )
  (sql/query db
    ["select * from surveys_images where dataset_id = ? and user_id = ?" dataset_id userid] ))

(defn get-gallery-previews []
  (sql/query db
             ["SELECT * FROM (SELECT *,row_number() OVER (partition by user_id) as row_number from surveys_images) as rows where row_number = 1"]))

(defn get-datasets [user_id]
  (sql/query db
             ["SELECT * FROM surveys_datasets WHERE user_id = ?" user_id]))

(defn create-dataset [dataset]
    (:id (first (sql/insert! db :surveys_datasets dataset))))

(defn delete-image [userid name]
  (sql/delete! db :surveys_images ["user_id = ? and name = ?" userid name]))

(defn delete-user [userid]
  (sql/delete! db :users ["username = ?" userid]))
