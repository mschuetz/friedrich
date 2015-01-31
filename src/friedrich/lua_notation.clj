(ns friedrich.lua-notation
  (:require [instaparse.core :as i]
            [instaparse.combinators :as c]))

(def lua-grammar "
  Program = (SPACE* Assignment)* SPACE*
  <Value> = Number | Bool | String | Path | Map

  Assignment = VarName <#'\\s*=\\s*'> Value
  VarName = #'[a-zA-Z][a-zA-Z0-9]+'

  Map = <#'\\{\\s*'> Pairs <#'\\s*\\}'>
  Pairs = Pair (<#'\\s*,\\s*'> Pair)* | ε
  Pair = PairKey <#'\\s*=\\s*'> PairValue
  PairKey = <#'\\[\\s*'> (Value) <#'\\s*\\]'>
  PairValue = Value

  String = <'\"'> #'[^\\\"]*' <'\"'>
  Number = #'[0-9]+'
  Path = #'/[^\\s]*'
  Bool = 'false' | 'true'
  <SPACE> = <#'\\s'>
")

(def parse (i/parser lua-grammar))
