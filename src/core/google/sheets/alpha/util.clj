(ns google.sheets.alpha.util
  (:require
   [clojure.string :as str]
   ))


(def ^:const alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ")


(defn parse-range
  [r]
  {:post [(<= (:row-start-index %) (:row-end-index %))
          (<= (:column-start-index %) (:column-end-index %))]}
  (let [index                      (str/index-of r "!")
        [start end]                (-> r (subs (inc index)) (str/upper-case) (str/split #":"))
        [column-start [row-start]] (split-with #(<= 65 (int %) 90) start)
        [column-end [row-end]]     (split-with #(<= 65 (int %) 90) end)]
    {:sheet-name         (subs r 0 index)
     :row-start-index    (if row-start (parse-long (str row-start)) 0)
     :row-end-index      (if row-end (parse-long (str row-end)) Integer/MAX_VALUE)
     :column-start-index (reduce (fn [ret c] (+ (* ret 26) (- (int c) 64))) 0 column-start)
     :column-end-index   (reduce (fn [ret c] (+ (* ret 26) (- (int c) 64))) 0 column-end)}))
