(ns google.sheets.alpha-test
  (:require
   [clojure.java.io :as jio]
   [clojure.test :as test :refer [deftest is are testing]]
   [google.sheets.alpha :as sheets]
   )
  (:import
   com.google.api.services.sheets.v4.Sheets
   ))


(def TEST_SPREADSHEET_ID (or (System/getenv "TEST_SPREADSHEET_ID") (slurp (jio/resource "test_spreadsheet_id"))))
(def TEST_SERVICE_ACCOUNT_JSON (or (System/getenv "TEST_SERVICE_ACCOUNT_JSON") (slurp (jio/resource "test_service_account.json"))))


(def ^Sheets sheets (sheets/build-sheets (str ::app) (sheets/google-credential (.getBytes TEST_SERVICE_ACCOUNT_JSON))))


(comment
  (-> (sheets/get-spreadsheet-info sheets TEST_SPREADSHEET_ID) (get "sheets") first (get-in ["properties" "sheetId"]))
  (get-in (sheets/get-spreadsheet-info sheets TEST_SPREADSHEET_ID) ["sheets"])
  )


(deftest main
  (is (= (get-in (sheets/get-spreadsheet-info sheets TEST_SPREADSHEET_ID) ["properties" "title"]) "io.github.ajchemist/google.sheets"))
  (is (= (sheets/get-sheet-id-from-sheet-name (sheets/get-spreadsheet-info sheets TEST_SPREADSHEET_ID) "TEST") 266636456))
  (prn
    (.. sheets (spreadsheets) (values) (get TEST_SPREADSHEET_ID "A:Z") (execute)))
  )


(comment
  (.. sheets
      (spreadsheets)
      (values)
      (batchUpdate TEST_SPREADSHEET_ID (doto (BatchUpdateValuesRequest.)
                                         (.setValueInputOption "USER_ENTERED")
                                         (.setData [(doto (ValueRange.)
                                                      (.setRange "A2:Z")
                                                      (.setValues [["2022-01-03" "2" "z"]
                                                                   ["2022-09-12T15:34:20" "b" "c"]]))])))
      (execute))


  (.. sheets
      (spreadsheets)
      (values)
      (batchUpdate TEST_SPREADSHEET_ID (doto (BatchUpdateValuesRequest.)
                                         (.setValueInputOption "USER_ENTERED")
                                         (.setData (doto (ValueRange.)
                                                     (.setRange "A2:Z")
                                                     (.setValues [["2022-01-03" "2" "z"]
                                                                  ["2022-09-12T15:34:20" "b" "c"]])))))
      (execute))


  (.setStartIndex (.setDimension (DimensionRange.) "ROWS") (int 1))
  )
