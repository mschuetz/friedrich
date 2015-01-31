(ns friedrich.lua-notation
  (:require [instaparse.core :as i]
            [instaparse.combinators :as c]))

(def lua-grammar "
  Program = (SPACE* Assignment* SPACE*)*
  <Value> = Map | String | Number | Bool | Path

  Assignment = VarName SPACE* <'='> SPACE* Value
  VarName = #'[a-zA-Z][a-zA-Z0-9]+'

  Map = <'{'> SPACE* Pair-List SPACE* <'}'>
  Pair-List = (Pair SPACE* ( <','> SPACE* Pair )*) | Epsilon
  Pair = PairKey SPACE* <'='> SPACE* PairValue
  PairKey = <'['> SPACE* (Value) SPACE* <']'>
  PairValue = Value

  String = <'\"'> #'[^\\\"]*' <'\"'>
  Number = #'[0-9]+'
  Path = ('/' #'[^/\\s]*')*
  Bool = 'false' | 'true'
  <SPACE> = <#'\\s' | #'$'>
")

(def parse (i/parser lua-grammar))
