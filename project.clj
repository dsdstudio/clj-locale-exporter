(defproject locale-exporter "0.1.0"
  :description "locale exporter"
  :url "http://github.com/dsdstudio/clj-locale-exporter"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.google.gdata/core "1.47.1"]
                 [cheshire "5.6.3"]
                 [google-apps-clj "0.5.1"]]
  :plugins [[cider/cider-nrepl "0.12.0"]]
  :main ^:skip-aot locale-exporter.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
