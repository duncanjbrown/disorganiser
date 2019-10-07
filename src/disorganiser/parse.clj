(ns disorganiser.parse
  (:require [opennlp.nlp :as nlp]
            [opennlp.treebank :as treebank]))

(defonce get-sentences (nlp/make-sentence-detector "models/en-sent.bin"))
(defonce tokenize (nlp/make-tokenizer "models/en-token.bin"))
(def detokenize (nlp/make-detokenizer "models/english-detokenizer.xml"))
(defonce pos-tag (nlp/make-pos-tagger "models/en-pos-maxent.bin"))
(defonce chunker (treebank/make-treebank-chunker "models/en-chunker.bin"))

(defn sentence->tagged-words [sentence]
  (let [tagged-entities (pos-tag (tokenize sentence))]
    (map #(zipmap [:word :pos] %)
         tagged-entities)))

(defn tagged-words->words [tagged-words]
  (map :word tagged-words))

(defn tagged-words->sentence [tagged-words]
  (detokenize (map :word tagged-words)))
