(ns geostitcher.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]
            [hiccup.form :refer :all]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]))

(defn utf-8-response [html]
  (content-type (response html) "text/html; charset=utf-8"))

(defn base [& content]
  (html5
    [:head
     [:title "GeoStitcher"]
     (include-css "/css/screen.css")
     (include-js "//code.jquery.com/jquery-2.0.2.min.js")]
    [:body content]))

(defn make-menu [& items]
  [:div (for [item items] [:div.menuitem item])])

(defn guest-menu []
  (make-menu
    (link-to "/" "home")
    (link-to "/register" "register")
    (form-to [:post "/login"]
                (text-field {:placeholder "screen name"} "username")
                (password-field {:placeholder "password"} "password")
                (submit-button "login"))))

(defn user-menu [user]
  (make-menu
    (link-to "/" "home")
    (link-to "/upload" "upload")
    (link-to "/logout" (str "logout " user))))

(defn common [& content]
  (base
    (if-let [user (session/get :username)]
      (user-menu user)
      (guest-menu))
    [:div.content content]))
