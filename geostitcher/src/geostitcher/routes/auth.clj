(ns geostitcher.routes.auth
  (:require [compojure.core :refer :all]
            [geostitcher.routes.home :refer :all]
            [geostitcher.views.layout :as layout]
            [geostitcher.models.db :as db]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [noir.util.route :refer [restricted]]
            [geostitcher.routes.upload :refer [delete-image]]
            [geostitcher.util :refer [gallery-path]])
  (:import java.io.File))

(defn create-gallery-path []
  (let [user-path (File. (gallery-path))]
    (if-not (.exists user-path) (.mkdirs user-path))
      (str (.getAbsolutePath user-path) File/separator)))

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
  (layout/render "registration.html"
                 {:username username
                  :username-error (first (vali/get-errors :username))
                  :password-error (first (vali/get-errors :password))}))

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
    
    (create-gallery-path)
    (resp/redirect "/")
    (catch Exception ex 
      (println ex)
      (vali/rule false [:username (format-error username ex)])
  (registration-page)))
  (registration-page username))))

(defn handle-login [username password]
  (let [user (db/get-user username)] 
    (if (and user (crypt/compare password (:password user)))
      (do
        (session/put! :username username)
        (session/put! :user_id (:id user)) )))
  (println "SESIJA USERID" (session/get :user_id))
  (resp/redirect "/"))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defn delete-account-page []  
  (layout/render "deleteAccount.html"))


;;TO DO CHANGE TO DELETE ALL PICTURES AND DATASETS OF THIS USER
(defn handle-confirm-delete []
  (let [user (session/get :username)]
    (println "DELETE ACCOUNT")
    (doseq [{:keys [name]} (db/images-from-dataset user)]
      (delete-image user name))
    (clojure.java.io/delete-file (gallery-path))
    (db/delete-user user)
    (session/clear!)
    (resp/redirect "/")))


(defroutes auth-routes
  (GET "/register" []
       (registration-page))
  
  (POST "/register" request (handle-registration request))
  
  (POST "/login" [username password]
        (handle-login username password))
  
  (GET "/logout" [] (handle-logout))
  
  (GET "/delete-account" []
       (restricted (delete-account-page)))
  
  (POST "/confirm-delete" []
        (restricted (handle-confirm-delete))))
