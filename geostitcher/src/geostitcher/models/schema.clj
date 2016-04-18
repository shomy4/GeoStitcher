(ns geostitcher.models.schema
  (:require [geostitcher.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/db-do-commands db
     (sql/create-table-ddl :users
                            {:username    "VARCHAR(50) PRIMARY KEY"
                             :password    "VARCHAR(100)"
                             :first_name  "VARCHAR(50)"
                             :last_name   "VARCHAR(50)"
                             :occupation  "VARCHAR(50)"
                             :place       "VARCHAR(50)"
                             :country     "VARCHAR(50)"})))

