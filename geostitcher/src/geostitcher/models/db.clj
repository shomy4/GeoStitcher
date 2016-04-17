(ns geostitcher.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/geostitcher"
         :user "postgres"
         :password "postgres"})

(defn create-user [user]
    (sql/insert! db :users user))

(defn get-user [username]
  (first
  (sql/query db
    ["select * from users where username = ?" username] )))




