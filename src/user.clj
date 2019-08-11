(ns user
  (:require [integrant.core :as ig]
            [disorganiser.core]
            [integrant.repl :refer [clear go halt prep init reset reset-all]]))

(integrant.repl/set-prep! (constantly disorganiser.core/system-config))
