(ns locale-exporter.core
  (:require clojure.pprint
            [cheshire.core :refer :all])
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
  (println "usage : java -jar locale-exporter.jar properties <sheet-id> <directory-path>")
  (System/exit -1))

(defn write-json [file-name data]
    (generate-stream data (clojure.java.io/writer file-name) {:pretty true}))


(defn to-properties-str [coll]
  (clojure.string/join "\n" (map #(clojure.string/join "=" %1) coll)))

(defn to-locale-coll
  "
  {:key {:ko <val> :en <val>} :key ....} 
  기존 raw data collection 을 가공하기 편한형태로 변환한다. 
  [
    {:locale [{:key :val} ... ]}
    {:locale [{:key :val} ... ]}
  ] 
  "
  [data locale-coll]
  (map (fn [locale]
         [locale (for [[k m] data]
           [k (get m locale)])]) locale-coll))


(defn write-properties [file-prefix data]
  (let [locale-data (to-locale-coll data ["ko" "en" "ja" "tw" "th" "zh"])
        file-ext ".properties"]
    (doseq [[k v] locale-data]
      (with-open [w (clojure.java.io/writer (str file-prefix "_" k file-ext))]
            (.write w (to-properties-str v))))))

(defn arguments-not-enough? [args]
  (not= 3 (count args)))

(defn is-json? [file-type]
  (= "json" file-type))

(defn is-properties? [file-type]
  (= "properties" file-type))

(defn -main [& args]
  (if (arguments-not-enough? args) (print-usage))
  (let [file-type (first args)
        sheet-id (second args)
        file-name (nth args 2)]
    (if (is-json? file-type) (write-json file-name (feed-list sheet-id "string")))
    (if (is-properties? file-type) (write-properties file-name (feed-list sheet-id "string")))))
