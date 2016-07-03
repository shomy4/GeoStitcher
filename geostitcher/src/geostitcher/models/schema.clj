(ns geostitcher.models.schema
  (:require [geostitcher.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/db-do-commands db
     (sql/create-table-ddl :auth_users
                            {:id     "SERIAL PRIMARY KEY"
                             :username    "VARCHAR(50)"
                             :password    "VARCHAR(100)"
                             :first_name  "VARCHAR(50)"
                             :last_name   "VARCHAR(50)"
                             :occupation  "VARCHAR(50)"
                             :place       "VARCHAR(50)"
                             :country     "VARCHAR(50)"})))

(defn create-dataset-table []
  (sql/db-do-commands db
     (sql/create-table-ddl :surveys_datasets
                           {:id     "SERIAL PRIMARY KEY"
                            :name "VARCHAR(100)"
                            :height "INTEGER"
                            :camera "VARCHAR(50)"
                            :user_id  " INTEGER REFERENCES auth_users (id)"})))


(defn create-images-table []
  (sql/db-do-commands db
     (sql/create-table-ddl :surveys_images
                           {:id     "SERIAL PRIMARY KEY"
                            :name "VARCHAR(100)"
                            :user_id " INTEGER REFERENCES auth_users (id)"
                            :dataset_id " INTEGER REFERENCES surveys_datasets (id)"
                            })))

