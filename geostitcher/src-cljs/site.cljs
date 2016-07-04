(ns site
  (:require 
    [domina :refer [by-class nodes sel attr]]
    [domina.css :refer [sel]]))

(defn ^:export init[]
  (js/alert "ClojureScript says 'Boo!'"))