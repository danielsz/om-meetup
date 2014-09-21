(ns user
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [environ.core :refer [env]]))

(def base-url "http://api.meetup.com/2")

(def results-xform
  (map (comp 
        #(json/read-str % :key-fn keyword) 
        :body)))

(defn rsvped-users []
  (let [rsvp-url (str base-url "/rsvps?event_id=200987152&order=name&key=" (:api-key env))]
    (->> [@(http/get rsvp-url)]
         (into {} results-xform)                     
         (:results))))

(defn users [] 
  (let [urls (map #(str base-url "/member/" (:member_id (:member %)) "?key=" (:api-key env)) (rsvped-users))
        futures (doall (map http/get urls))
        results (doall (map deref futures))]
    (sequence results-xform results)))

(defn app-state [] 
  (let [users (users)
        topics (map #(map :name %)(map :topics users))
        users (map #(select-keys % [:name :id :bio :photo :city :link]) users)
        coll (partition 2 (interleave users topics))]
    {:users (for [x coll]
              (assoc (first x) :topics (into [] (last x))))}))
