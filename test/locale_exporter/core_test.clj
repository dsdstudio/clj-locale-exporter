(ns locale-exporter.core-test
  (:require [clojure.test :refer :all]
            [locale-exporter.core :refer :all]))

(def sid "1Yl4MIohwNJMgsnDrEnGFUc1lxxbEsqv6N-b4QMCRovs")

(deftest predicate-test
  (testing "is-json 테스트"
    (is (is-json? "json"))
    (is (not (is-json? "asdf"))))
  (testing "is-properties? 테스트"
    (is (is-properties? "properties"))
    (is (not (is-properties? "qewrqwr")))))
