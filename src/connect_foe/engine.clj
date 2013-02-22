(ns connect-foe.engine)

(defprotocol IConnectFourGrid
  (valid-move? [grid move])
  (make-move-no-check [grid move val]
    "Makes the move without a validity check.")
  (valid-moves [grid]
    "Returns the set of valid moves."))

(defn make-move
  "Makes a move if the move is valid; otherwise returns nil."
  [grid move val]
  (when (valid-move? grid move)
    (make-move-no-check grid move val)))

(extend-type clojure.lang.PersistentVector
  IConnectFourGrid
  (valid-move? [grid column]
    (and (< -1 column 7)
         (< (count (grid column)) 6)))

  (make-move-no-check [grid column val]
    (assoc grid column (conj (get grid column) val)))

  (valid-moves [grid]
    (filter (partial valid-move? grid) (range 7))))

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

(defprotocol IConnectFourPlayer
  (next-move [player]
    "The next move, a number 0-6 representing the column."))

(defrecord RandomPlayer [grid]
  IConnectFourPlayer
  (next-move [_]
    (rand-nth (valid-moves grid))))

(comment
  (let [grid (->VectorGrid)]
    (time (doseq [_ (range 1e6)]
            (valid-move? grid 0))))

  (let [grid (->VectorGrid)]
    (time (doseq [_ (range 1e6)]
            (make-move-no-check grid 0 :val))))

  (let [grid (->VectorGrid)]
    (time (doseq [_ (range 1e5)]
            (vec (valid-moves grid)))))

  )
