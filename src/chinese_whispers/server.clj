(ns chinese-whispers.server
  (:require [lamina.core :as l] [aleph.tcp :as tcp]))

(defn hello-world [channel request]
  (enqueue channel
    {:status 200
     :headers {"content-type" "text/html"}
     :body "Hello World!"}))

(tcp/start-tcp-server hello-world {:port 8008})

