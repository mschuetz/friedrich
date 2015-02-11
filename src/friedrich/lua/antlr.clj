(ns friedrich.lua.antlr
  (:require [clj-antlr.core :as antlr]))

(def parse (antlr/parser (slurp (clojure.java.io/resource "friedrich/lua/lua.g4"))))

