(defproject geostitcher "0.1.0-SNAPSHOT"
  :description "Aerial images stitching applications"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/java.jdbc "0.6.0-alpha1"]
                 [clj-pdf "1.11.6"]
                 [com.taoensso/timbre "3.1.6"]
                 [lib-noir "0.9.8"]
                 [selmer "1.0.7"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-cljsbuild "1.1.3"]]
  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["src-cljs"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          :output-to "war/javascripts/main.js"  ; default: target/cljsbuild-main.js
          :optimizations :whitespace
          :pretty-print true}}]}
  :ring {:handler geostitcher.handler/app
         :init geostitcher.handler/init
         :destroy geostitcher.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring/ring-mock "0.3.0"] 
                   [ring/ring-devel "1.6.0-beta1"] 
                   [org.clojure/clojurescript "0.0-3308"]
                   [opencv/opencv "2.4.11"]
                   [opencv/opencv-native "2.4.11"]]}}
  :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)])
