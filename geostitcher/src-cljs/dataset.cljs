(ns dataset
  (:require [goog.dom :as dom]
            [domina :refer [by-id by-class nodes append!]]
            [domina.events :refer [listen!]]
            [domina.css :refer [sel]]
            [ajax.core :refer [POST]]))


(defn click_thumbnail[_]
  (js/alert "You clicked thumbnail"))

(defn ^:export init[]
  
  (listen! (by-class "ds_image_thumb") :click click_thumbnail))