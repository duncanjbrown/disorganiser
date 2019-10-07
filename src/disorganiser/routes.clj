(ns disorganiser.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.anti-forgery :as csrf]
            [disorganiser.xforms :as xforms]
            [hiccup.core :refer [html]]
            [hiccup.form :as hform]
            [clojure.string :as str]))

(defn input
  [text {:keys [lines-per-verse strip-adjectives mangle-nouns intensity]}]
  [:form {:action "/disorganise" :method "POST"}
    [:fieldset
     (csrf/anti-forgery-field)
     [:label "Input"]
     [:label
      [:textarea {:name "input"} text]]
     [:label "Strip adjectives "
      (hform/check-box "options[strip-adjectives]" strip-adjectives)]
     [:label "Mangle nouns "
      (hform/check-box "options[mangle-nouns]" mangle-nouns)]
     [:label "Lines per verse "
      (hform/drop-down "options[lines-per-verse]" (range 1 11) (Integer/parseInt lines-per-verse))]
     ;; [:label "Intensity "
     ;;  (hform/drop-down "options[intensity]" ["low" "high"])]
     [:button {:type "submit"} "Go"]]])

(defn output [text]
  [:fieldset
   [:label "Output"]
   [:textarea {:name "output" :style "height: 500px"} text]])

(defn app-view
  ([]
   (app-view "" "" {:lines-per-verse "5"}))
  ([input-text output-text options]
   (html
    [:html
     [:head
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:title "Song Disorganiser"]
      [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css-1.5.1.min.css"}]]
     [:body
      [:h1 "Song Disorganiser"]
      (input input-text options)
      (output output-text)]])))

(def available-transforms
  {:strip-adjectives xforms/strip-adjectives
   :mangle-nouns xforms/swap-nouns})

(defn disorganise [input-text options]
  (println options)
  (let [transforms (select-keys available-transforms (keys options))
        xform-args (conj (vals transforms) input-text)
        output-text (-> (apply xforms/xform-multiline xform-args)
                        (xforms/versify (Integer/parseInt (:lines-per-verse options))))]
    (app-view input-text output-text options)))

(defroutes app
  (GET "/" [] (app-view))
  (POST "/disorganise" [input options] (disorganise input options))
  (route/not-found "<h1>Page not found</h1>"))
