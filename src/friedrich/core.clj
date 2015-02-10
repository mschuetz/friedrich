(ns friedrich.core
  (:require [pandect.algo.md5 :refer [md5]]
            [org.httpkit.client :as hc]
            [hickory.core :as hickory]
            [hickory.select :as hs]
            [clojurewerkz.urly.core :as urly]
            [clojure.string :as str]
            [friedrich.insta :as lua])
  (:import (java.util.concurrent TimeoutException)
           (java.nio.charset StandardCharsets)))

(declare ^:dynamic *session*)
(def ^:dynamic *base-uri* "http://fritz.box/")

(defmacro with-session [session & body]
  `(binding [*session* ~session]
     ~@body))

(defn- on-success
  ([success-fn] (on-success [200] success-fn))
  ([allowed-status success-fn]
    (fn [{:keys [status error] :as resp}]
      (if error
        error
        (if (not (.contains allowed-status status))
          (IllegalStateException. (str "received status: " status " allowed: " allowed-status))
          (success-fn resp))))))

(defn get-challenge [login-uri]
  (hc/get login-uri
          (on-success (fn [{:keys [body]}]
                        (second (re-find #"g_challenge = \"([a-fA-F0-9]+)\"" body))))))

(defn- throw-if-exception [val]
  (if (instance? Throwable val)
    (throw val)
    val))

(defn deref-or-throw
  ([promise]
    (throw-if-exception (deref promise)))
  ([promise timeout]
    (throw-if-exception (deref promise timeout (TimeoutException. (str "timeout was " timeout))))))

(defn- login-uri []
  (str *base-uri* "login.lua")
  )

(defn- challenge-response [challenge password]
  (str challenge "-" (md5 (.getBytes (str challenge "-" password) StandardCharsets/UTF_16LE))))

(defn- query-param
  "extracts the given query parameter from the uri"
  [param uri]
  ((into {} (filter #(= param (first %1))
                    (map #(str/split %1 #"=")
                         (str/split (:query (urly/as-map uri)) #"&"))))
    param))

(defn login
  "fritzbox login, returns session id"
  ([password]
    (login password (deref-or-throw (get-challenge (login-uri)))))
  ([password challenge]
    (hc/post (login-uri)
             {:form-params      {"response" (challenge-response challenge password)}
              :follow-redirects false}
             (on-success [303] (fn [{:keys [headers]}]
                                 (query-param "sid" (:location headers)))))))
(defn list-devices
  ([] (list-devices *session*))
  ([session]
    (hc/get (str *base-uri* "net/network_user_devices.lua")
            {:query-params {"sid" session}}
            (on-success (fn [{:keys [body]}]
                          ;#logqueries > pre:nth-child(1)
                          (->
                            (hs/select
                              (hs/child (hs/id "logqueries") hs/first-child)
                              (hickory/as-hickory (hickory/parse body)))
                            first :content first lua/parse lua/as-map))))))