(defproject wiki-talk-parser "0.2.1"
  :description "Parse wikipedia dump files (xml) to wiki-talk networks."
  :url "http://github.com/yfiua/wiki-talk-parser"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]]
  :main ^:skip-aot wiki-talk-parser.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
