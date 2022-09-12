(ns google.sheets.alpha
  (:require
   [clojure.java.io :as jio]
   )
  (:import
   com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
   com.google.api.client.googleapis.auth.oauth2.GoogleCredential
   com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
   com.google.api.client.json.JsonFactory
   com.google.api.client.json.gson.GsonFactory
   com.google.api.services.sheets.v4.Sheets$Builder
   com.google.api.services.sheets.v4.Sheets
   com.google.api.services.sheets.v4.SheetsScopes
   com.google.api.services.sheets.v4.model.ValueRange
   com.google.api.services.sheets.v4.model.Request
   com.google.api.services.sheets.v4.model.InsertDimensionRequest
   com.google.api.services.sheets.v4.model.DimensionRange
   com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
   com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
   ))


(set! *warn-on-reflection* true)


(defn- get-json-factory
  ^JsonFactory
  []
  (GsonFactory/getDefaultInstance))


(defn client-secrets
  ^GoogleClientSecrets
  [path]
  (with-open [rdr (jio/reader (jio/resource path))]
    (GoogleClientSecrets/load (get-json-factory) rdr)))


(defn google-credential
  ^GoogleCredential
  [input]
  (with-open [is (jio/input-stream input)]
    (. (GoogleCredential/fromStream is)
       (createScoped [SheetsScopes/SPREADSHEETS]))))


(defn build-sheets
  ^Sheets
  [^String app-name ^GoogleCredential credential]
  (.. (Sheets$Builder. (GoogleNetHttpTransport/newTrustedTransport) (get-json-factory) credential)
      (setApplicationName (or app-name "io.github.ajchemist/google.sheets"))
      (build)))


(defn get-spreadsheet-info
  [^Sheets sheets spreadsheet-id]
  (.. sheets (spreadsheets) (get spreadsheet-id) (execute)))


(defn get-sheet-id-from-sheet-name
  [info sheet-name]
  (some
    (fn [{{title "title"} "properties" :as sheet}]
      (and (= title sheet-name)
           (get-in sheet ["properties" "sheetId"])))
    (get info "sheets")))


(defn value-range
  ^ValueRange
  [{range "range"
    values "values"}]
  (let [^ValueRange o (ValueRange.)]
    (.setRange o range)
    (.setValues o values)
    o))


(defn dimension-range
  ^DimensionRange
  [{^String dimension "dimension"
    start-index       "startIndex"
    end-index         "endIndex"
    sheet-id          "sheetId"}]
  (let [^DimensionRange o (DimensionRange.)]
    (when dimension (.setDimension o dimension))
    (when start-index (.setStartIndex o (int start-index)))
    (when end-index (.setEndIndex o (int end-index)))
    (when sheet-id (.setSheetId o (int sheet-id)))
    o))


(defn insert-dimension-request
  ^InsertDimensionRequest
  [{range "range"}]
  (let [^InsertDimensionRequest o (InsertDimensionRequest.)]
    (.setRange o (dimension-range range))
    o))


(defn request
  ^Request
  [{insert-dimension "insertDimension"}]
  (let [^Request o (Request.)]
    (when insert-dimension (.setInsertDimension o (insert-dimension-request insert-dimension)))
    o))


(defn batch-update-spreadsheet-request
  ^BatchUpdateSpreadsheetRequest
  [requests]
  (doto (BatchUpdateSpreadsheetRequest.)
    (.setRequests requests)))


(defn batch-update-values-request
  ^BatchUpdateValuesRequest
  [{data                       "data"
    ^String value-input-option "valueInputOption"}]
  (let [^BatchUpdateValuesRequest o (BatchUpdateValuesRequest.)]
    (.setValueInputOption o value-input-option)
    (.setData o (value-range data))
    o))


(set! *warn-on-reflection* false)
