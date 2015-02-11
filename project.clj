(defproject friedrich "0.1.0-SNAPSHOT"
            :description "some code for interacting with a fritzbox (tm)"
            :url "http://github.com/mschuetz/friedrich"
            :license {:name "MIT License"
                      :url  "https://www.tldrlegal.com/l/mit"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [hickory "0.5.4"]
                           [http-kit "2.1.19"]
                           [instaparse "1.3.5"]
                           [pandect "0.5.1"]
                           [clojurewerkz/urly "1.0.0"]
                           [clj-antlr "0.2.2"]]
            :profiles {:dev {:dependencies
                             [[criterium "0.3.1"]]}})
