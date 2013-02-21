(ns connect-foe.engine)

(defn ->Grid
  "Constructs an empty 7x6 Connect Four grid, represented by a
  length-42 vector. Index 0 represents the top left corner of
  the board."
  []
  (vec (repeat 42 nil)))

(defn valid-move?
  "Given a Connect Four grid and an index, determines whether
  the move represented by the index is allowed."
  [board idx]
  (and
   ;; the move is in bounds
   (< -1 idx (count board))
   ;; the location is empty
   (not (board idx))
   ;; the move is on the bottom row
   (or (<= 35 idx 41)
       ;; or there is a piece under it
       (board (+ 7 idx)))))

