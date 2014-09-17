(ns user
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [environ.core :refer [env]]))


(def base-url "http://api.meetup.com/2")

(defn rsvped-users [] 
  (let [rsvp-url (str base-url "/rsvps?event_id=200987152&order=name&key=" (:api-key env))]
                        (-> @(http/get rsvp-url)
                            (:body)
                            (json/read-str :key-fn keyword)
                            (:results))))

(def results-xform
  (map (comp 
        #(json/read-str % :key-fn keyword) 
        :body)))

(defn users [] 
  (let [urls (map #(str base-url "/member/" (:member_id (:member %)) "?key=" (:api-key env)) (rsvped-users))
                     futures (doall (map http/get urls))
                     results (doall (map deref futures))]
                 (sequence results-xform results)))

(defn app-state [] 
  {:users (mapv #(select-keys % [:name :bio :photo :city :link]) (users))})


