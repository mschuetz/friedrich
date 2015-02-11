(ns friedrich.lua.insta
  (:require [instaparse.core :as i]
            [instaparse.combinators :as c])
  (:import (sun.reflect.generics.reflectiveObjects NotImplementedException)))

(def lua-grammar "
  program = (SPACE* assignment)* SPACE*
  <value> = number | bool | string | path | map

  assignment = varname <#'\\s*=\\s*'> value
  varname = #'[a-zA-Z][a-zA-Z0-9]+'

  map = <#'\\{\\s*'> pairs <#'\\s*\\}'>
  <pairs> = pair (<#'\\s*,\\s*'> pair)* | ε
  pair = pairkey <#'\\s*=\\s*'> pairvalue
  <pairkey> = <#'\\[\\s*'> (value) <#'\\s*\\]'>
  <pairvalue> = value

  string = <'\"'> #'[^\\\"]*' <'\"'>
  number = #'[0-9]+'
  path = #'/[^\\s]*'
  bool = 'false' | 'true'
  <SPACE> = <#'\\s'>
")

(def parse (i/parser lua-grammar))

(defn- parse-varname [[key value]]
  (assert (= :varname key))
  (keyword value))

; TODO try to parse as double, bigint, etc. if it fails
(defn- parse-number [strvalue]
  (Long/parseLong strvalue))

(declare parse-value)

(defn- parse-pair [[type key value]]
  (assert (= :pair type))
  [(parse-value key) (parse-value value)])

(defn- parse-map [pairs]
  (into {} (map parse-pair pairs)))

(defn- parse-value [[type & values]]
  (case type
    :string (first values)
    :path (first values)
    :bool (Boolean/parseBoolean (first values))
    :number (parse-number (first values))
    :map (parse-map values)))

(defn- parse-assignment [[key & rest]]
  (assert (= :assignment key))
  [(parse-varname (first rest))
   (parse-value (second rest))])

(defn- parse-program [[key & assignments]]
  (assert (= :program key))
  (map parse-assignment assignments))

(defn as-map [node]
  (case (first node)
    :program (into {} (parse-program node))
    (throw (Exception. "expected :Program"))))