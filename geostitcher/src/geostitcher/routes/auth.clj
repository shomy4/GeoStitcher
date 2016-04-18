(ns geostitcher.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [geostitcher.routes.home :refer :all]
            [geostitcher.views.layout :as layout]
            [geostitcher.models.db :as db]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]))

(defn format-error [username ex]
  (cond
    (and (instance? org.postgresql.util.PSQLException ex)
         (= 0 (.getErrorCode ex)))
    (str "The user with id " username " already exists!")
    :else
    "An error has occured while processing the request"))

(defn error-item [[error]]
  [:div.error error])

(defn control [id label field]
  (list
    (vali/on-error id error-item)
    label field
    [:br]))



(defn valid? [form-params]
  (vali/rule (vali/has-value? (:username form-params))
             [:username "user id is required"])
  (vali/rule (vali/min-length? (:password form-params) 5)
             [:password "password must be at least 5 characters"])
  (vali/rule (= (:password form-params) (:password1 form-params))
             [:password "entered passwords do not match"])
  (not (vali/errors? :username :password :password1)))


(defn registration-page [& [username]]
  (layout/base
    (form-to [:post "/register"]
             (control :username
                      (label "username" "Username")
                      (text-field {:tabindex 1} "username" username))
             
             (control :password
                      (label "password" "Password")
                      (password-field {:tabindex 2} "password"))
             (control :password1
                      (label "password1" "Password")
                      (password-field {:tabindex 3} "password1"))
             (control :first_name
                      (label "first_name" "First name")
                      (text-field {:tabindex 4} "first_name"))
             (control :last_name
                      (label "last_name" "Last name")
                      (text-field {:tabindex 5} "last_name"))
             (control :occupation
                      (label "occupation" "Occupation")
                      (text-field {:tabindex 6} "occupation"))
             (control :place
                      (label "place" "Place")
                      (text-field {:tabindex 7} "place"))
             (control :country
                      (label "country" "Country")
                      (text-field {:tabindex 8} "country"))
             (submit-button {:tabindex 9} "Create account"))))

(defn handle-registration [request]
  (let [form-params (:params request)
        username (:username form-params)
        password (crypt/encrypt (:password form-params))
        first_name (:first_name form-params)
        last_name (:last_name form-params)
        occupation (:occupation form-params)
        place (:place form-params)
        country (:country form-params)]
    (if (valid? form-params)
  (try
    (db/create-user {:username username :password password :first_name first_name :last_name last_name
                     :occupation occupation :place place :country country})
    (session/put! :username username)
    (resp/redirect "/")
    (catch Exception ex 
      (vali/rule false [:username (format-error username ex)])
  (registration-page)))
  (registration-page username))))

(defn handle-login [username password]
  (let [user (db/get-user username)]
    (if (and user (crypt/compare password (:password user)))
      (session/put! :username username)))
  (resp/redirect "/"))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (registration-page))
  
  (POST "/register" request (handle-registration request))
  
  (POST "/login" [username password]
        (handle-login username password))
  
  (GET "/logout" [] (handle-logout)))
