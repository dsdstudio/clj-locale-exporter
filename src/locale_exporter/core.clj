(ns locale-exporter.core
  (:require clojure.pprint)
  (:import (com.google.gdata.client.spreadsheet FeedURLFactory SpreadsheetService)
           (com.google.gdata.data.spreadsheet WorksheetFeed ListFeed CustomElementCollection))
  (:gen-class))

(defn worksheet-url [sheet-id]
  (.getWorksheetFeedUrl (FeedURLFactory/getDefault) sheet-id "public" "basic"))

(defn worksheet-service [sheet-id]
  (new SpreadsheetService "spreadsheet"))

(defn worksheet-feed-map [sheet-id]
  (let [service (worksheet-service sheet-id)]
    (reduce #(assoc %1 (.getPlainText (.getTitle %2)) %2) {}
            (.getEntries (.getFeed service (worksheet-url sheet-id) WorksheetFeed)))))

; customelement 에 아무것도 바인딩 되지않는문제가 있어서 content 영역 데이터를 파싱함
(defn list-feed [sheet-id sheet-key]
  (let [word-work-sheet (get (worksheet-feed-map sheet-id) sheet-key)
        service (worksheet-service sheet-id)
        list-entries (-> service
                         (.getFeed (.getListFeedUrl word-work-sheet) ListFeed)
                         (.getEntries))]
    (->> list-entries
         (map #(let [id (.getPlainText (.getTitle %1))
                     locale-data (into {} (map vec (partition 2 (clojure.string/split (.getPlainTextContent %1) #": |, "))))]
                 (assoc locale-data "id" id)))
         (filter #(not-empty (get %1 "id")))
         (filter #(not (clojure.string/starts-with? (get %1 "id") "#"))))))

(defn -main [& args]
  (let [sheet-id (first args)]
    (if (nil? sheet-id)
      (println "usage : java -jar locale-exporter.jar <sheet-id>")
      (clojure.pprint/print-table (list-feed (first args) "string")))))

