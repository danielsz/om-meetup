(ns meetup.core                         
  (:require 
   [meetup.users :refer [users]]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [secretary.core :as secretary :include-macros true :refer [defroute]]
   [cljs-utils.core :as utils]
   [cljs.core.async :as async :refer [<! >! put! take! chan]]
   [kioo.om :refer [html content set-attr do-> substitute listen add-class set-class]]
   [goog.history.EventType :as EventType])
  (:require-macros 
   [cljs.core.async.macros :as a :refer [go go-loop]]
   [kioo.om :refer [defsnippet deftemplate]])
  (:import [goog.history Html5History]))

(enable-console-print!)

(def history (Html5History.))

(doto history
  (.setUseFragment false)
  (.setPathPrefix "") 
  (.setEnabled true))

(let [navigation (utils/listen history EventType/NAVIGATE)]
  (go 
    (while true
      (let [token (.-token (<! navigation))]
        (secretary/dispatch! token)))))

(defonce app-state 
  (atom
   {:brand ["CUG Tel-Aviv" #(.setToken history "/")]
    :navigation [["Users" #(.setToken history "/users")]
                 ["About" #(.setToken history "/about")]]
    :welcome {:title "Clojure Israel"
              :caption "RSVPed users for the next meetup (Hoplon)."}
    :about {:title "Demo"
            :caption "This is a demo"}
    :view :root
    :users users}))

(defn define-routes [app-state]
  
  (defroute "/" {:as params}
    (om/update! app-state :view :root))

  (defroute "/about" {:as params}
    (om/update! app-state :view :about))

  (defroute "/users" {:as params}
    (om/update! app-state :view :users))
  
  (defroute user-path "/user/:id" [id]
    (om/update! app-state :view id)))

;;

(defsnippet my-nav-item "templates/header.html" [:li]
  [[caption fn]]
  {[:a] (do-> (content caption)
              (listen :onClick fn))})

(defsnippet my-brand "templates/header.html" [:.navbar-header]
  [[caption fn]]
  {[:a] (do-> (content caption)
              (listen :onClick fn))})

(defsnippet my-header "templates/header.html" [:nav]
  [{:keys [navigation brand]}]
  {[:.navbar-header] (content (my-brand brand))
   [:ul] (content (map my-nav-item navigation))})

;;

(defn thumbnail [data]
  (if-let [picture (:thumb_link (:photo data))]
    picture
    "http://photos3.meetupstatic.com/photos/member/d/0/7/4/thumb_11573364.jpeg"))

(defn to-slug [name]
  (-> name
      (clojure.string/split #"\s")
      (#(clojure.string/join "-" %))
      (clojure.string/lower-case)))

(defn slugify [users]
  (mapv (fn [user] (assoc user :slug (to-slug (:name user)))) users))

;;

(defn user
  "Om component for new user"
  [data owner]
  (reify
    om/IDisplayName
    (display-name [this]
      "user")
    om/IRender
    (render [_]
      (html [:.media.well [:a.pull-left {:href (:link data)}
                              [:img.media-object {:src (:photo_link (:photo data))}]]
             [:.media-body [:h4 (:name data)]
              [:p (:city data)]
              [:p (:bio data)]
              [:ul
               (for [topic (:topics data)]
                 [:li topic])]]]))))

(defn user-thumbnail
  "Om component for thumbnail"
  [data owner]
  (reify
    om/IDisplayName
    (display-name [this]
      "user-thumbnail")
    om/IRender
    (render [_]
      (html [:.thumbnail {:on-click (fn [e]
                                      (.setToken history (user-path {:id (to-slug (:name @data))})))} 
             [:img {:src (thumbnail data)}]
             [:.caption
              [:h5 (:name data)]]]))))

(defn users
  "Om component for new users"
  [data owner]
  (reify
    om/IDisplayName
    (display-name [this]
      "users")
    om/IRender
    (render [_]
      (html [:div.row.well#users-container (om/build-all user-thumbnail data {:key :name})]))))


(defn about
  "Om component for new about"
  [data owner]
  (reify
    om/IDisplayName
    (display-name [this]
      "about")
    om/IRender
    (render [_]
      (html [:div.row
             [:div.col-md-6.col-sm-12.well
              [:h1 (:title (:about data))]
              [:p (:caption (:about data))]]]))))

(defn welcome
  "Om component for new welcome"
  [data owner]
  (reify
    om/IDisplayName
    (display-name [this]
      "welcome")
    om/IRender
    (render [_]
      (html [:div.row
             [:div.col-md-6.col-sm-12.jumbotron
              [:h1 (:title (:welcome data))]
              [:p (:caption (:welcome data))]]]))))

(defn application
  "Om component for new application"
  [data owner]
  (reify
    om/IWillMount
    (will-mount [this]
      (define-routes data))
    om/IDisplayName
    (display-name [this]
      "application")
    om/IRender
    (render [_]
      (dom/div nil
               (dom/div nil (my-header data))
               (let [view (:view data)]
                 (condp = view
                   :root (om/build welcome data)
                   :users (om/build users (:users data) {:react-key "users"})
                   :about (om/build about data)
                   (om/build user (some #(when (= (:slug %) view) %) (slugify (:users data))) {:key :name})))))))

(om/root application app-state {:target (utils/by-id "om")})
