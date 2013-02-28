(ns connect-foe.engine
  (:require [connect-foe.client :as client]
            [clojure.string :as string]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Helpers

(defn replace-index [s index character]
  (str (subs s 0 index) character (subs s (inc index))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Protocols

(defprotocol IConnectFourGrid
  (valid-move? [grid move])
  (valid-moves [grid]
    "Returns the set of valid moves.")
  (make-move [grid move val]
    "Makes the move if it's valid, or returns nil.")
  (won? [grid]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Grids

(extend-type clojure.lang.PersistentVector
  IConnectFourGrid
  (valid-move? [grid column]
    (and (< -1 column 7)
         (< (count (grid column)) 6)))

  (valid-moves [grid]
    (filter (partial valid-move? grid) (range 7)))

  (make-move [grid column val]
    (when (valid-move? grid column)
      (assoc grid column (conj (get grid column) val)))))

(defn ->VectorGrid
  "Constructs a 7x6 Connect Four grid, represented by a length-7
  vector of 7 vectors, where each inner vector is a column with
  index 0 representing the bottom row."
  ([]
     (vec (repeat 7 [])))
  ([moves]
     (reduce (fn [grid [column val]]
               (make-move grid column val))
             (->VectorGrid)
             moves)))

(extend-type String
  IConnectFourGrid
  (valid-move? [grid column]
    (= \space (nth grid column)))

  (valid-moves [grid]
    (filter (partial valid-move? grid) (range 7)))

  (make-move [grid column val]
    (when (valid-move? grid column)
      (let [index (first (filter #(= \space (nth grid %))
                                 (range (+ column 35) 0 -7)))]
        (replace-index grid index val))))

  (won? [grid]
    (let [delimited-grid (string/join "|" (mapv #(apply str %)
                                                (partition 7 grid)))
          horizontal #"[^\| ]{4,}"
          vertical   #"(.{7}r){4}|(.{7}b){4}"
          r-diagonal #"(.{6}r){4}|(.{6}b){4}"
          l-diagonal #"(.{8}r){4}|(.{8}b){4}"]
      (some #(re-find % delimited-grid)
            [horizontal vertical r-diagonal l-diagonal]))))

(defn ->StringGrid
  "Constructs a 7x6 Connect Four grid, represented by a length-42
  string where index 0 is the top left corner of the grid."
  []
  (apply str (repeat 42 \space)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Players

(defrecord RandomPlayer []
  client/IArenaClient
  (move [_ message]
    (rand-nth (valid-moves (:board message))))
  (begin [_ message]
    (println "Beginning random play."))
  (game-over [_ message]
    (println "Game over."))
  (game-type [_]
    "connectfour"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Runner

(defn -main [& [ai host port]]
  (let [ai (case ai
             "random" (->RandomPlayer))
        host (or host "localhost")
        port (or (and port (int port)) 4000)]
    (client/issue-challenge ai host port)))



(comment 
  (let [grid (->VectorGrid)]
    (time (doseq [_ (range 1e6)]
            (valid-move? grid 0))))
  
  (let [grid (->VectorGrid)]
    (time (doseq [_ (range 1e5)]
            (vec (valid-moves grid)))))
  )
