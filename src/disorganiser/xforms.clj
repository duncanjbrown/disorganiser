(ns disorganiser.xforms
  (:require [disorganiser.parse :as parse]))

(defn- adjective? [tagged-word]
  (some? (re-find #"^JJ" (:pos tagged-word))))

(defn- noun? [tagged-word]
  (some? (re-find #"^NN" (:pos tagged-word))))

(defn- replace-pred [replacements pred coll]
  "Given a set of replacements, a predicate and a collection,
   return a new collection of the same length with the elements
   matching pred replaced by a succession of values from replacements"
  (let [current (first coll)]
    (if (empty? replacements)
      coll
      (let [next (if (pred current) (first replacements) current)
            remaining-replacements (if (pred current) (rest replacements) replacements)]
        (conj (replace-pred remaining-replacements pred (rest coll))
              next)))))

(defn swap-nouns [tagged-words]
  "Shuffle nouns in a list of tagged words"
  (let [nouns (filter noun? tagged-words)]
    (replace-pred (shuffle nouns) noun? tagged-words)))

(defn strip-adjectives [tagged-words]
  "Remove adjectives from a list of tagged words"
  (filter (complement adjective?) tagged-words))

(defn xform [sentence & ops]
  (let [xforms (apply comp ops)]
    (-> (parse/sentence->tagged-words sentence)
        xforms
        parse/tagged-words->sentence)))
