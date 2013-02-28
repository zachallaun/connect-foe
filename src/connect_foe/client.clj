(ns connect-foe.client
  (:require [clojure.data.json :as json])
  (:import [java.net Socket]
           [java.io PrintWriter InputStreamReader BufferedReader]))

(defprotocol IArenaClient
  (begin [engine message])
  (move [engine message])
  (game-over [engine message])
  (game-type [engine]))

(defn open-on [host port]
  (let [socket (Socket. host port)
        out (PrintWriter. (.getOutputStream socket))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))]
    {:sock socket :out out :in in}))

(defn listen [conn handler]
  (future
    (while true
      (let [message (.readLine (:in conn))]
        (println message)
        (handler conn message)))))

(defn write-to [^PrintWriter out msg]
  (println msg)
  (doto out
    (.println msg)
    (.flush)))

(defn make-move [move token]
  (json/write-str {:move move :token token}))

(defn dispatch-to [engine conn message]
  (let [message (json/read-str message :key-fn keyword)]
    (println message)
    (cond
      (contains? message :timelimit)
      (begin engine message)

      (contains? message :history)
      (game-over engine message)

      :default
      (write-to (:out conn)
                (json/write-str {:move (move engine message)
                                 :token (:token message)})))))

(defn issue-challenge [engine host port]
  (let [conn (open-on host port)
        listener (listen conn (partial dispatch-to engine))]
    (write-to (:out conn) (json/write-str {:game (game-type engine)}))
    (assoc conn :listener listener)))
