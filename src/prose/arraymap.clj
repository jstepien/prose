(ns prose.arraymap)

(def db (atom {}))

(defn init-db
  []
  (swap! db (fn [_] {})))

(defn save-db
  []
  (do
    (spit prose.core/*db-name* (with-out-str (print-dup @db *out*)))
    true))

(defn load-db
  []
  (do
    (swap! db (fn [_] (with-in-str (slurp prose.core/*db-name*) (read))))
    true))

(defn insert
  [rows]
  (if (not (empty? rows))
    (let [row (first rows)
          prev (first row)
          next (second row)]
      (swap! db #(assoc % prev (conj (get % prev) next)))
      (recur (rest rows)))))

(defn rand-elem
  [coll]
  (nth coll (rand-int (count coll))))

(defn random-beginning
  []
  (first (rand-elem (lazy-seq @db))))

(defn match
  [what]
  (rand-elem (get @db what)))
