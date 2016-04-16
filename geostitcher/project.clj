(defproject geostitcher "0.1.0-SNAPSHOT"
  :description "Aerial images stitching applications"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/java.jdbc "0.6.0-alpha1"]
                 [clj-pdf "1.11.6"]
                 [lib-noir "0.7.6"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler geostitcher.handler/app
         :init geostitcher.handler/init
         :destroy geostitcher.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
