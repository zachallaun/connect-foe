(ns connect-foe.engine)

(defprotocol IConnectFourGrid
  (valid-move? [grid move]
    "`move` is an [x y] pair.")
  (make-move-no-check [grid move val]
    "Makes the move without a validity check."))

(defn make-move
  "Makes a move if the move is valid; otherwise returns nil."
  [grid move val]
  (when (valid-move? grid move)
    (make-move-no-check grid move val)))

(extend-type clojure.lang.PersistentVector
  IConnectFourGrid
  (valid-move? [board [x y]]
    (let [idx (+ (long x) (* (long y) 7))]
      (and
       ;; the move is in bounds
       (< -1 idx (count board))
       ;; the location is empty
       (not (board idx))
       ;; the move is on the bottom row
       (or (<= 35 idx 41)
           ;; or there is a piece under it
           (board (+ 7 idx))))))

  (make-move-no-check [board [x y] val]
    (assoc board (+ (long x) (* (long y) 7)) val)))

(defn ->VectorGrid
  "Constructs a 7x6 Connect Four grid, represented by a length-42
  vector. Index 0 represents the top left corner of the board.

  init is an optional map of indicies to values used to populate
  the grid."
  ([] (->VectorGrid []))
  ([init]
     (reduce (fn [grid [idx val]]
               (assoc grid idx val))
             (vec (repeat 42 nil))
             init)))
