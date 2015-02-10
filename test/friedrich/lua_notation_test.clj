(ns friedrich.lua-notation-test
  (:require [clojure.test :refer :all]
            [friedrich.insta :refer :all]
            [criterium.core :refer [bench]]))

(def testinput
  "
  bar = {
     [\"UID\"] = \"landevice4229\",
     [\"_node\"] = \"landevice0\",
     [\"active\"] = false
  }
  baz = {}
  foo = true
  quux = 123
  ")

(def minimal "
baz = 123
")

(def large (slurp (str (System/getenv "HOME") "/test.lua")))

(deftest perf-test
  ;(time (dotimes [_ 20] (parse (slurp (str (System/getenv "HOME") "/test.lua")))))
  ;(time (dotimes [_ 10] (parse (slurp (str (System/getenv "HOME") "/test.lua")))))
  ;(bench (parse minimal))
  ;(bench (parse large))
  ;(time (parse testinput))
  )

(def very-simple "
foo=\"bar\"
baz= 123
quux=false
")

(deftest test-as-map-assignments
  (testing ""
    (is (= (as-map (parse very-simple)) {:foo "bar" :baz 123 :quux false}))))

(def with-map "
foo = \"bar\"
baz = {
  [\"quux\"] = false,
  [\"1\"] = {
    [\"2\"] = 3,
    [\"4\"] = {}
  }
}
")

(deftest test-as-map-with-map
  (testing ""
    (is (= (as-map (parse with-map)) {:foo "bar" :baz {"quux" false "1" {"2" 3 "4"{}}}}))))
