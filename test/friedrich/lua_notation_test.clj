(ns friedrich.lua-notation-test
  (:require [clojure.test :refer :all]
            [friedrich.lua-notation :refer :all]
            ))

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

;(deftest perf-test
;  (time (parse (slurp (str (System/getenv "HOME") "/test.lua"))))
;  (time (parse testinput)))

(def very-simple "
foo = \"bar\"
baz = 123
quux = false
")

(deftest test-as-map-assignments
  (testing ""
    (is (= (as-map (parse very-simple)) {:foo "bar" :baz 123 :quux false}))))

(def with-map "
foo = \"bar\"
baz = {
  [\"quux\"] = false,
  [\"1\"] = {
    [\"2\"] = 3
  }
}
")

(deftest test-as-map-with-map
  (testing ""
    (is (= (as-map (parse with-map)) {:foo "bar" :baz {"quux" false "1" {"2" 3}}}))))
