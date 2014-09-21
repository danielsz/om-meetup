(ns meetup.core                         
  (:require 
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
    :users
    [{:link "http://www.meetup.com/members/10935314",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/1/8/0/8/member_28506152.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/1/8/0/8/thumb_28506152.jpeg",
       :photo_id 28506152},
      :bio "I'm always interested in meeting new people.",
      :name "Adam"}
     {:link "http://www.meetup.com/members/157664872",
      :city "Tel Aviv-Yafo",
      :name "Adi Shacham-Shavit"}
     {:link "http://www.meetup.com/members/127886162",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/c/a/d/2/member_201291922.jpeg",
       :highres_link
       "http://photos2.meetupstatic.com/photos/member/c/a/d/2/highres_201291922.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/c/a/d/2/thumb_201291922.jpeg",
       :photo_id 201291922},
      :name "Akiv Solomon"}
     {:link "http://www.meetup.com/members/98633202",
      :city "Giv'atayim",
      :photo
      {:photo_link
       "http://photos2.meetupstatic.com/photos/member/8/0/6/8/member_153752872.jpeg",
       :highres_link
       "http://photos4.meetupstatic.com/photos/member/8/0/6/8/highres_153752872.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/8/0/6/8/thumb_153752872.jpeg",
       :photo_id 153752872},
      :name "Alon Rolnik"}
     {:link "http://www.meetup.com/members/80976022",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/7/e/d/8/member_149972472.jpeg",
       :thumb_link
       "http://photos1.meetupstatic.com/photos/member/7/e/d/8/thumb_149972472.jpeg",
       :photo_id 149972472},
      :name "Alon Rozental"}
     {:link "http://www.meetup.com/members/34203992",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/9/3/b/4/member_171817812.jpeg",
       :highres_link
       "http://photos2.meetupstatic.com/photos/member/9/3/b/4/highres_171817812.jpeg",
       :thumb_link
       "http://photos4.meetupstatic.com/photos/member/9/3/b/4/thumb_171817812.jpeg",
       :photo_id 171817812},
      :name "Amitay Horwitz"}
     {:link "http://www.meetup.com/members/67841272",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/c/7/6/c/member_91731052.jpeg",
       :highres_link
       "http://photos3.meetupstatic.com/photos/member/c/7/6/c/highres_91731052.jpeg",
       :thumb_link
       "http://photos3.meetupstatic.com/photos/member/c/7/6/c/thumb_91731052.jpeg",
       :photo_id 91731052},
      :name "Andrew Skiba"}
     {:link "http://www.meetup.com/members/63580692",
      :city "Tel Aviv-Yafo",
      :name "Daniel Slutsky"}
     {:link "http://www.meetup.com/members/16392291",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/b/1/5/9/member_14625401.jpeg",
       :highres_link
       "http://photos4.meetupstatic.com/photos/member/b/1/5/9/highres_14625401.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/b/1/5/9/thumb_14625401.jpeg",
       :photo_id 14625401},
      :name "Daniel Szmulewicz"}
     {:link "http://www.meetup.com/members/13998696",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos2.meetupstatic.com/photos/member/9/9/7/2/member_201279282.jpeg",
       :highres_link
       "http://photos4.meetupstatic.com/photos/member/9/9/7/2/highres_201279282.jpeg",
       :thumb_link
       "http://photos4.meetupstatic.com/photos/member/9/9/7/2/thumb_201279282.jpeg",
       :photo_id 201279282},
      :name "Evgeny Budilovski"}
     {:link "http://www.meetup.com/members/58301072",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/5/7/8/2/member_150802402.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/5/7/8/2/thumb_150802402.jpeg",
       :photo_id 150802402},
      :name "Gal Topper"}
     {:link "http://www.meetup.com/members/90702112",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/e/5/f/8/member_113038872.jpeg",
       :thumb_link
       "http://photos1.meetupstatic.com/photos/member/e/5/f/8/thumb_113038872.jpeg",
       :photo_id 113038872},
      :name "Kosta Kliakhandler"}
     {:link "http://www.meetup.com/members/16506801",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/8/a/f/3/member_14735571.jpeg",
       :thumb_link
       "http://photos1.meetupstatic.com/photos/member/8/a/f/3/thumb_14735571.jpeg",
       :photo_id 14735571},
      :name "Matanster Saf"}
     {:link "http://www.meetup.com/members/45468802",
      :city "Ramat Gan",
      :photo
      {:photo_link
       "http://photos2.meetupstatic.com/photos/member/4/2/6/member_165601062.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/4/2/6/thumb_165601062.jpeg",
       :photo_id 165601062},
      :name "naty"}
     {:link "http://www.meetup.com/members/35227792",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos2.meetupstatic.com/photos/member/6/b/3/2/member_175167442.jpeg",
       :thumb_link
       "http://photos4.meetupstatic.com/photos/member/6/b/3/2/thumb_175167442.jpeg",
       :photo_id 175167442},
      :name "Nir Asis"}
     {:link "http://www.meetup.com/members/75694882",
      :city "Modi'in",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/2/4/1/a/member_90669242.jpeg",
       :thumb_link
       "http://photos4.meetupstatic.com/photos/member/2/4/1/a/thumb_90669242.jpeg",
       :photo_id 90669242},
      :name "Nir Cohen"}
     {:link "http://www.meetup.com/members/94082112",
      :city "Tel Aviv-Yafo",
      :name "Rotem Erlich"}
     {:link "http://www.meetup.com/members/13630778",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/d/0/7/4/member_11573364.jpeg",
       :highres_link
       "http://photos3.meetupstatic.com/photos/member/d/0/7/4/highres_11573364.jpeg",
       :thumb_link
       "http://photos3.meetupstatic.com/photos/member/d/0/7/4/thumb_11573364.jpeg",
       :photo_id 11573364},
      :name "Tzach Livyatan"}
     {:link "http://www.meetup.com/members/60939522",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/6/a/d/8/member_95367352.jpeg",
       :thumb_link
       "http://photos3.meetupstatic.com/photos/member/6/a/d/8/thumb_95367352.jpeg",
       :photo_id 95367352},
      :name "Yehonathan Sharvit"}
     {:link "http://www.meetup.com/members/36956112",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos4.meetupstatic.com/photos/member/c/9/6/member_203523222.jpeg",
       :thumb_link
       "http://photos4.meetupstatic.com/photos/member/c/9/6/thumb_203523222.jpeg",
       :photo_id 203523222},
      :name "yonatan"}
     {:link "http://www.meetup.com/members/95801842",
      :city "Kholon",
      :photo
      {:photo_link
       "http://photos2.meetupstatic.com/photos/member/a/d/8/6/member_121544422.jpeg",
       :highres_link
       "http://photos4.meetupstatic.com/photos/member/a/d/8/6/highres_121544422.jpeg",
       :thumb_link
       "http://photos2.meetupstatic.com/photos/member/a/d/8/6/thumb_121544422.jpeg",
       :photo_id 121544422},
      :name "Yuri Klayman"}
     {:link "http://www.meetup.com/members/8696190",
      :city "Tel Aviv-Yafo",
      :photo
      {:photo_link
       "http://photos1.meetupstatic.com/photos/member/c/6/a/7/member_10610855.jpeg",
       :highres_link
       "http://photos3.meetupstatic.com/photos/member/c/6/a/7/highres_10610855.jpeg",
       :thumb_link
       "http://photos3.meetupstatic.com/photos/member/c/6/a/7/thumb_10610855.jpeg",
       :photo_id 10610855},
      :name "Zvi Avraham"}]}))

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
              [:p (:bio data)]]]))))

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
