(ns disorganiser.xforms-test
  (:require
   [disorganiser.xforms :as sut]
   [clojure.test :as t]))

(t/deftest strip-adjectives
  (t/testing "Removing adjectives"
    (t/is
     (= [{:word "The", :pos "DT"} {:word "boy", :pos "NN"}]
        (sut/strip-adjectives
         [{:word "The", :pos "DT"} {:word "good", :pos "JJ"} {:word "better", :pos "JJR"} {:word "best", :pos "JJS"} {:word "boy", :pos "NN"}])))))

(t/deftest swap-nouns
  (t/testing "Swapping nouns"
    (t/is
     (let [words [{:word "Winter", :pos "NNP"} {:word "summer", :pos "NN"}]
           swaps (take 10 (repeatedly #(sut/swap-nouns words)))]
       (= false (apply = swaps))))
    (t/is
     (let [words [{:word "girl", :pos "NN"} {:word "boy", :pos "NN"}]
           swaps (take 10 (repeatedly #(sut/swap-nouns words)))]
       (= false (apply = swaps))))))

(t/deftest xform
  (t/testing "Single transformation"
    (t/is
     (= "dog bites Man"
        (sut/xform "Man bites dog" reverse)))))
