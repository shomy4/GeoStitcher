(ns geostitcher.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/geostitcher"
         :user "postgres"
         :password ""})

(defn create-user [user]
    (sql/insert! db :users user))

(defn get-user [username]
  (first
  (sql/query db
    ["select * from users where username = ?" username] )))

(defn add-image [userid name]
  (if-not (first (sql/query db
                            ["SELECT userid from images where userid = ? and name = ?" userid name]))
    (sql/insert! db :images {:userid userid :name name})
  (throw (Exception. "You have already uploaded an image with the exact same name"))))

(defn images-by-user [userid]
  (sql/query db
    ["select * from images where userid = ?" userid] ))

(defn get-gallery-previews []
  (sql/query db
             ["SELECT * FROM (SELECT *,row_number() OVER (partition by userid) as row_number from images) as rows where row_number = 1"]))

(defn delete-image [userid name]
  (sql/delete! db :images ["userid = ? and name = ?" userid name]))

(defn delete-user [userid]
  (sql/delete! db :users ["username = ?" userid]))
