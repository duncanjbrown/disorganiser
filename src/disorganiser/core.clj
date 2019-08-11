(ns disorganiser.core
  (:require [disorganiser.routes :as routes]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]))

(def system-config
  (ig/read-string (slurp "system.edn")))

(def handler
  (-> routes/app
      (wrap-defaults site-defaults)))

(defmethod ig/init-key :adapter/jetty [_ opts]
  (jetty/run-jetty #'handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))
