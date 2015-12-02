(defproject wiki-talk-parser "0.1.0"
  :description "Parse wikipedia dump files (xml) to wiki-talk networks."
  :url "http://github.com/yfiua/wiki-talk-parser"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.xml "0.0.8"]]
  :main ^:skip-aot wiki-talk-parser.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
