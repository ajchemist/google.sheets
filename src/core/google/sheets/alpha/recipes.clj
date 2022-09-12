(ns google.sheets.alpha.recipes
  (:require
   [clojure.string :as str]
   [google.sheets.alpha.util :as util]
   [google.sheets.alpha :as sheets]
   )
  (:import
   com.google.api.services.sheets.v4.Sheets
   ))


(set! *warn-on-reflection* true)


(defn insert-row-dimensions
  ([^Sheets sheets ^String spreadsheet-id sheet-id start-index n]
   (let [sheet-id (or
                    sheet-id
                    (-> (sheets/get-spreadsheet-info sheets spreadsheet-id) (get "sheets") first (get-in ["properties" "sheetId"])))]
     (.. sheets
         (spreadsheets)
         (batchUpdate spreadsheet-id (sheets/batch-update-spreadsheet-request
                                       [(sheets/request {"insertDimension"
                                                         {"range"
                                                          {"dimension"  "ROWS"
                                                           "sheetId"    sheet-id
                                                           "startIndex" start-index
                                                           "endIndex"   (+ start-index n)}}})]))
         (execute)))))


(defn insert-row-data
  [^Sheets sheets ^String spreadsheet-id range ^String value-input-option values]
  (let [{:keys [sheet-name row-start-index]} (util/parse-range range)
        sheet-id                             (when (string? sheet-name)
                                               (sheets/get-sheet-id-from-sheet-name
                                                 (sheets/get-spreadsheet-info sheets spreadsheet-id)
                                                 sheet-name))
        num                                  (count values)]
    (insert-row-dimensions sheets spreadsheet-id sheet-id (dec row-start-index) num)
    (.. sheets
        (spreadsheets)
        (values)
        (update spreadsheet-id range (sheets/value-range {"values" values}))
        (setValueInputOption (or value-input-option "USER_ENTERED"))
        (execute))))


(set! *warn-on-reflection* false)
