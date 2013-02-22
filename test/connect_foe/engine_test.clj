(ns connect-foe.engine-test
  (:use [midje.sweet]
        [connect-foe.engine]))

(defn all [& assertions]
  (every? identity assertions))

(defn vector-grid-facts []
  (all
   (fact "VectorGrids don't need an init"
         (->VectorGrid) => vector?)

   (fact "VectorGrids are length 7"
         (count (->VectorGrid)) => 7)

   (fact "VectorGrids can be initialized"
         (get (->VectorGrid [[0 :r]]) 0) => [:r])

   (fact "any non-full stack is valid"
         (valid-move? (->VectorGrid) 0) => truthy
         (valid-move? (->VectorGrid (repeat 5 [0 :r])) 0) => truthy)

   (fact "moving to a full stack is invalid"
         (valid-move? (->VectorGrid (repeat 6 [0 :r])) 0) => falsey)

   (fact "valid moves are within the grid's bounds"
         (valid-move? (->VectorGrid) -1) => falsey
         (valid-move? (->VectorGrid) 7) => falsey)

   (fact "make-move checks validity"
         (get (make-move (->VectorGrid) 0 :r) 0) => [:r]

         (-> (->VectorGrid (repeat 6 [0 :r]))
             (make-move 0 :r))
         => nil?

         (make-move (->VectorGrid) -1 :r) => nil?
         (make-move (->VectorGrid) 7 :r) => nil?)

   (fact "can generate valid moves"
         (set (valid-moves (->VectorGrid))) => #{0 1 2 3 4 5 6}

         (set (valid-moves (->VectorGrid (repeat 6 [0 :r]))))
         => #{1 2 3 4 5 6})))
