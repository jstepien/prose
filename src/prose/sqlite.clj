(ns prose.sqlite
  (:require [clojure.contrib.sql :as sql]
            [clojure.contrib.str-utils2 :as str2]))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname prose.core/*db-name*
         :create true})

(defmacro with-db
  [body]
  `(sql/with-connection db ~body))

(defn init-db
  []
  (with-db (do
             (sql/do-commands
               "drop table if exists chains")
             (sql/create-table
               :chains
               [:previous (str "varchar(" prose.core/*prev-length* \))]
               [:next "varchar(1)"])
             (sql/do-commands
               "create index indexed_previous on chains (previous)"))))

(defn random-beginning
  []
  (with-db
    (sql/with-query-results
      res
      ["SELECT previous, random() r FROM chains ORDER BY r LIMIT 1"]
      (:previous (first res)))))

(defn match
  [what]
  (with-db
    (sql/with-query-results
      res
      [(str "SELECT next, random() r FROM chains WHERE previous = '"
            (str2/replace what "'" "''")
            "' ORDER BY r LIMIT 1")]
      (:next (first res)))))

(defn insert
  [rows]
  (with-db
      (apply sql/insert-rows :chains rows)))

(defn load-db [] true)
(defn save-db [] true)
