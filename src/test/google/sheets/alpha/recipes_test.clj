(ns google.sheets.alpha.recipes-test
  (:require
   [clojure.test :as test :refer [deftest is are]]
   [google.sheets.alpha-test :as sheets.test :refer [TEST_SPREADSHEET_ID]]
   [google.sheets.alpha.recipes :as recipes]
   )
  (:import
   java.time.ZonedDateTime
   java.time.ZoneId
   java.time.format.DateTimeFormatter
   ))


(deftest main
  (recipes/insert-row-data
    sheets.test/sheets
    TEST_SPREADSHEET_ID
    "GITHUB_ACTIONS!A2:Z"
    "USER_ENTERED"
    [[(.format DateTimeFormatter/ISO_LOCAL_DATE_TIME (ZonedDateTime/now (ZoneId/of "Asia/Seoul")))]])

  )


(comment
  (recipes/insert-row-dimensions sheets.test/sheets TEST_SPREADSHEET_ID nil 1 1)
  )
