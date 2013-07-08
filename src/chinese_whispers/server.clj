(ns chinese-whispers.server
  (:require [lamina.core :as l] 
            [aleph.tcp :as tcp]
            [gloss.core :refer [string]]))

(def clients (atom []))

(defn get-next-ch [channel]
  (get (into {} (map vec (partition 2 1 @clients))) channel))

(defn connection-recd [channel client-info]
  (swap! clients conj channel)
  (l/receive-all channel 
                 (fn [msg]
                   (if-let [next-ch (get-next-ch channel)]
                      (l/enqueue next-ch msg)
                      (println msg)))))

(defn start-whispering [msg]
  (l/enqueue (first @clients) msg))

(defn start-server []
  (tcp/start-tcp-server connection-recd {:port 8008 :frame (string :utf-8 :delimiters ["\n"])}))

(defn add-client [host port f]
  (let [ch (l/wait-for-result
               (tcp/tcp-client {:host "localhost",
                                :port 10000,
                                :frame (string :utf-8 :delimiters ["\n"])})])
    (l/receive-all ch f)
