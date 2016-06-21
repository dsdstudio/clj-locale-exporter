(ns locale-exporter.core
  (:require clojure.pprint
            [clojure.data.json :as json])
  (:import (com.google.gdata.client.spreadsheet FeedURLFactory SpreadsheetService)
           (com.google.gdata.data.spreadsheet WorksheetFeed ListFeed CustomElementCollection))
  (:gen-class))


(def sheet-service (new SpreadsheetService "spreadsheet"))

(defn sheet-url [sheet-id]
  (.getWorksheetFeedUrl (FeedURLFactory/getDefault) sheet-id "public" "basic"))

(defn sheet-map
  "시트에 대한 맵을 생성한다 
  {:string <some spreadsheet obj>
  ...}"
  [sheet-id]
    (reduce #(assoc %1 (.getPlainText (.getTitle %2)) %2) {}
            (.getEntries (.getFeed sheet-service (sheet-url sheet-id) WorksheetFeed))))

; customelement 에 아무것도 바인딩 되지않는문제가 있어서 content 영역 데이터를 파싱함
(defn feed-list [sheet-id sheet-key]
  (let [selected-sheet (get (sheet-map sheet-id) sheet-key)
        list-entries (-> sheet-service
                         (.getFeed (.getListFeedUrl selected-sheet) ListFeed)
                         (.getEntries))]
    (->> list-entries
         (map #(let [id (.getPlainText (.getTitle %1))
                     locale-data (into {} (map vec (partition 2 (clojure.string/split (.getPlainTextContent %1) #": |, "))))]
                 (assoc locale-data "id" id)))
         (filter #(not-empty (get %1 "id")))
         (filter #(not (clojure.string/starts-with? (get %1 "id") "#")))
         (reduce #(assoc %1 (get %2 "id") (dissoc %2 "id")) {}))))

(defn print-usage []
  (println "usage : java -jar locale-exporter.jar json <sheet-id> <file-name>")
  (println "usage : java -jar locale-exporter.jar properties <sheet-id> <directory-path>"))

(defn write-json [file-name data]
  (with-open [w (clojure.java.io/writer file-name :append true)]
    (.write w (json/write-str data))))

(defn -main [& args]
  (if (= 3 (count args))
    (let [file-type (first args)
          sheet-id (second args)
          file-name (nth args 2)]
      (if (or (nil? sheet-id) (nil? file-name))
        (print-usage)
        (write-json file-name (feed-list sheet-id "string"))))
  (print-usage)))
