(ns prose.core)

(def *prev-length* 10)
(def *db-name* "prose.db")

;;; Choose either SQLite or an array map
;(require '[prose.sqlite :as db])
(require '[prose.arraymap :as db])

(defn init-db
  []
  (db/init-db))

(defn save-db
  []
  (db/save-db))

(defn load-db
  []
  (db/load-db))

(defn- break-into-previous-and-next
  [string]
  [(apply str (take prose.core/*prev-length* string))
   (first (drop prose.core/*prev-length* string))])

(defn teach
  [string]
  (loop [rows '() data string]
    (if (> (count data) (inc *prev-length*))
      (recur (cons (break-into-previous-and-next data) rows)
             (rest data))
      (db/insert rows))))

(defn write
  [limit]
  (if (> limit *prev-length*)
    (loop [poem (db/random-beginning)
           left (- limit (count poem))]
      (if (zero? left)
        poem
        (recur (str poem (db/match (apply str (take-last *prev-length* poem))))
               (dec left))))))
