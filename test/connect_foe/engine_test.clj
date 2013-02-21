(ns connect-foe.engine-test
  (:use [midje.sweet]
        [connect-foe.engine]))

(fact "valid moves cannot float"
      (valid-move? (->Grid) 0)
      => falsey)

(fact "bottom row is a valid move"
      (valid-move? (->Grid) 41) => truthy
      (valid-move? (->Grid) 35) => truthy)

(fact "on top of another piece is a valid move"
      (valid-move? (assoc (->Grid) 38 :b) 31) => truthy)

(fact "same location as another piece is not a valid move"
      (valid-move? (assoc (->Grid) 38 :b) 38) => falsey)

(fact "valid moves are within the grid's bounds"
      (valid-move? (->Grid) -1) => falsey
      (valid-move? (->Grid) 42) => falsey)
