(ns user
  (:require
   [clojure.browser.repl :as repl]
   [figwheel.client :as fw :include-macros true]))

(repl/connect "http://localhost:9000/repl")
(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
;; (defonce app-data (atom {}))

(println "Edits to this text should show up in your developer console.")

(fw/watch-and-reload
 :jsload-callback (fn []
                    (print "reloaded")
                    ;; (stop-and-start-my app)
                    ))
