(ns user
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(def base-url "http://api.meetup.com/2")

(def rsvped-users
  (let [rsvp-url (str base-url "/rsvps?event_id=200987152&order=name&key=" (:api-key env))]
    (-> @(http/get rsvp-url)
        (:body)
        (json/read-str :key-fn keyword)
        (:results))))

(def users
  (let [urls (map #(str base-url "/member/" (:member_id (:member %)) "?key=" (:api-key env)) rsvped-users)
        futures (doall (map http/get urls))
        results (doall (map deref futures))]
    (map (comp #(json/read-str % :key-fn keyword) :body) results)))

(defn app-state [users] 
  (let [topics (map #(map :name %)(map :topics users))
        users (map #(select-keys % [:name :id :bio :photo :city :link]) users)
        coll (partition 2 (interleave users topics))]
    {:users (vec (for [x coll]
                   (assoc (first x) :topics (into [] (last x)))))}))

