(ns locale-exporter.core-test
  (:require [clojure.test :refer :all]
            [locale-exporter.core :refer :all]))

(def s-id "1Yl4MIohwNJMgsnDrEnGFUc1lxxbEsqv6N-b4QMCRovs")
(deftest a-test
  (testing "Simple assert Test"
    (is (not-empty s-id))))
