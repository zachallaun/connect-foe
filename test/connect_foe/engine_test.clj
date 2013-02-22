(ns connect-foe.engine-test
  (:use [midje.sweet]
        [connect-foe.engine]))

(defn all [& assertions]
  (every? identity assertions))

(defn vector-grid-facts []
  (all
   (fact "VectorGrids don't need an init"
         (->VectorGrid) => vector?)

   (fact "VectorGrids are length 42"
         (count (->VectorGrid)) => 42)

   (fact "VectorGrids can be initialized"
         ((->VectorGrid {0 :foobar}) 0) => :foobar)

   (fact "valid moves cannot float"
         (valid-move? (->VectorGrid) [0 0]) => falsey)

   (fact "bottom row is a valid move"
         (valid-move? (->VectorGrid) [0 5]) => truthy
         (valid-move? (->VectorGrid) [6 5]) => truthy)

   (fact "on top of another piece is a valid move"
         (valid-move? (assoc (->VectorGrid) 38 :b) [3 4]) => truthy)

   (fact "same location as another piece is not a valid move"
         (valid-move? (assoc (->VectorGrid) 38 :b) [3 5]) => falsey)

   (fact "valid moves are within the grid's bounds"
         (valid-move? (->VectorGrid) [0 -1]) => falsey
         (valid-move? (->VectorGrid) [7 5]) => falsey)

   (fact "make-move-no-check will make moves willy nilly"
         (-> (->VectorGrid)
             (make-move-no-check [0 0] :move)
             (nth 0))
         => :move

         (-> (->VectorGrid {0 :foo})
             (make-move-no-check [0 0] :bar)
             (nth 0))
         => :bar)

   (fact "make-move checks properly"
         (make-move (->VectorGrid) [0 0] :move) => nil?
         (make-move (->VectorGrid {41 :foo}) [6 5] :move) => nil?

         (-> (->VectorGrid {41 :foo})
             (make-move [6 4] :bar)
             (nth 34))
         => :bar)))
