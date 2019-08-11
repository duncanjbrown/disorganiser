(ns disorganiser.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.anti-forgery :as csrf]
            [disorganiser.xforms :as xforms]
            [hiccup.core :refer [html]]))

(defn input [text]
  [:form {:action "/disorganise" :method "POST"}
    [:fieldset
     (csrf/anti-forgery-field)
     [:label "Input"]
     [:label
        [:textarea {:name "input"} text]]
     [:button {:type "submit"} "Go"]]])

(defn output [text]
  [:fieldset
   [:label "Output"]
   [:textarea {:name "output"} text]])

(defn app-view
  ([]
   (app-view "" ""))
  ([input-text output-text]
   (html
    [:html
     [:head
      [:title "Song Disorganiser"]
      [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css-1.5.1.min.css"}]]
     [:body
      [:h1 "Song Disorganiser"]
      (input input-text)
      (output output-text)]])))

(defn disorganise [input-text]
  (app-view input-text (xforms/xform input-text
                                     xforms/strip-adjectives
                                     xforms/swap-nouns)))

(defroutes app
  (GET "/" [] (app-view))
  (POST "/disorganise" [input] (disorganise input))
  (route/not-found "<h1>Page not found</h1>"))
