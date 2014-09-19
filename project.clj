(defproject meetup "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0-alpha2"]
                 [org.clojure/clojurescript "0.0-2342"]
                 [com.facebook/react "0.11.2"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [sablono "0.2.22"]
                 [om "0.7.3"]
                 [org.danielsz/cljs-utils "0.1.0-SNAPSHOT"]
                 [secretary "1.2.1"]
                 [kioo "0.4.0"]]
  
  :profiles {:dev {:dependencies [[http-kit "2.1.19"]
                                  [org.clojure/tools.trace "0.7.8"]
                                  [environ "1.0.0"]]
                   :source-paths ["dev"]}}

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.1.4-SNAPSHOT"]]
  
  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src" "dev"]
              :compiler {:output-to "resources/public/js/compiled/meetup.js"
                         :output-dir "resources/public/js/compiled/out"
                         :optimizations :none
                         :source-map true}}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "www/meetup.min.js"
                         :optimizations :advanced
                         :pretty-print false
                         :preamble ["react/react.min.js"]
                         :externs ["react/externs/react.js"]}}]}
  :figwheel {
             :http-server-root "public" ;; default and assumes "resources" 
             :server-port 3449 ;; default
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             })
