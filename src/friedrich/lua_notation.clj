(ns friedrich.lua-notation
  (:require [instaparse.core :as i]
            [instaparse.combinators :as c])
  (:import (sun.reflect.generics.reflectiveObjects NotImplementedException)))

(def lua-grammar "
  Program = (SPACE* Assignment)* SPACE*
  <Value> = Number | Bool | String | Path | Map

  Assignment = VarName <#'\\s*=\\s*'> Value
  VarName = #'[a-zA-Z][a-zA-Z0-9]+'

  Map = <#'\\{\\s*'> Pairs <#'\\s*\\}'>
  <Pairs> = Pair (<#'\\s*,\\s*'> Pair)* | ε
  Pair = PairKey <#'\\s*=\\s*'> PairValue
  <PairKey> = <#'\\[\\s*'> (Value) <#'\\s*\\]'>
  <PairValue> = Value

  String = <'\"'> #'[^\\\"]*' <'\"'>
  Number = #'[0-9]+'
  Path = #'/[^\\s]*'
  Bool = 'false' | 'true'
  <SPACE> = <#'\\s'>
")

(def parse (i/parser lua-grammar))

(defn- parse-varname [[key value]]
  (assert (= :VarName key))
  (keyword value))

; TODO try to parse as double, bigint, etc. if it fails
(defn- parse-number [strvalue]
  (Long/parseLong strvalue))

(declare parse-value)

(defn- parse-pair [[type key value]]
  (assert (= :Pair type))
  [(parse-value key) (parse-value value)])

(defn- parse-map [pairs]
  (into {} (map parse-pair pairs)))

(defn- parse-value [[type & values]]
  (case type
    :String (first values)
    :Path (first values)
    :Bool (Boolean/parseBoolean (first values))
    :Number (parse-number (first values))
    :Map (parse-map values)))

(defn- parse-assignment [[key & rest]]
  (assert (= :Assignment key))
  [(parse-varname (first rest))
   (parse-value (second rest))])

(defn- parse-program [[key & assignments]]
  (assert (= :Program key))
  (map parse-assignment assignments))

(defn as-map [node]
  (case (first node)
    :Program (into {} (parse-program node))
    (throw (Exception. "expected :Program"))))