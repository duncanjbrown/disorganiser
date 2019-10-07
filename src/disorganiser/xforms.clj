(ns disorganiser.xforms
  (:require [disorganiser.parse :as parse]
            [clojure.string :as str]))

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

(defn strip-adjectives
  "Remove adjectives from a list of tagged words"
  [tagged-words]
  (filter (complement adjective?) tagged-words))

(defn words->lines
  "Given a list of words, break them into lines of approximate
  length per-line. Will jitter +/- one word every 10 or so lines."
  [words per-line]
  (let [distribution (map + (repeat per-line) [-1 0 0 0 0 0 0 0 0 0 0 +1])
        words-this-line #(rand-nth distribution)]
    (loop [words words
           lines []
           n (words-this-line)]
      (if (empty? words)
        (map (partial str/join " ") lines)
        (recur (drop n words)
             (conj lines (take n words))
             (words-this-line))))))

(defn xform-multiline [sentence & ops]
  (let [xforms (apply comp ops)]
    (-> (str/split-lines sentence)
        (shuffle)
        (#(str/join " " %))
        (parse/sentence->tagged-words)
        xforms
        parse/tagged-words->words
        (words->lines 5))))

(defn versify [lines n]
  (str/join (flatten (-> (partition-all
                            (* 2 n)
                            (interleave lines (repeat "\n")))
                         (interleave (repeat "\n"))))))



(defn xform [sentence & ops]
  (let [xforms (apply comp ops)]
    (-> (parse/sentence->tagged-words sentence)
        xforms
        parse/tagged-words->sentence)))

                                        ; to transform a text with many lines
                                        ; consider the lines as one text
                                        ; transform that text
