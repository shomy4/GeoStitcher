(ns geostitcher.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]
            [hiccup.form :refer :all]))

(defn base [& content]
  (html5
    [:head
     [:title "GeoStitcher"]
     (include-css "/css/screen.css")]
    [:body content]))

(defn common [& content]
  (base
    (if-let [user (session/get :username)]
      [:div (link-to "/logout" (str "logout " user))]
      [:div (link-to "/register" "register")
       (form-to [:post "/login"]
                (text-field {:placeholder "screen name"} "username")
                (password-field {:placeholder "password"} "password")
                (submit-button "login"))])
    content))
