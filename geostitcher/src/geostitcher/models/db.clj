(ns geostitcher.models.db
  (:require [clojure.java.jdbc :as sqls]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/geostitcher"
         :user "postgres"
         :password "postgres"})




