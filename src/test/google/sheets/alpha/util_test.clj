(ns google.sheets.alpha.util-test
  (:require
   [clojure.test :as test :refer [deftest is are testing]]
   [google.sheets.alpha.util :as util]
   ))


(deftest main
  (is (= (:sheet-name (util/parse-range "시트1!A:Z")) "시트1"))
  (is (= (:row-start-index (util/parse-range "시트1!A2:Z")) 2))
  (is (= (:column-end-index (util/parse-range "시트1!A2:Z")) 26))
  (is (= (:column-end-index (util/parse-range "시트1!A2:AB3")) 28))
  (is (= (try
           (util/parse-range "시트1!Z2:X3")
           (catch java.lang.AssertionError _ 'AssertionError))
         'AssertionError))
  )
