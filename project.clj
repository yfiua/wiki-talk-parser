(defproject wiki-talk-parser "0.4.2"
  :description "Parse wikipedia dump files (xml) to wiki-talk networks."
  :url "http://github.com/yfiua/wiki-talk-parser"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot wiki-talk-parser.core
  :target-path "target/%s"
  :auto-clean false
  :profiles {:parser {:main wiki-talk-parser.core
                      :dependencies [[org.clojure/data.json "0.2.6"]
                                     [clj-http "2.0.0"]]
                      :uberjar-name "parser.jar"}
             :shrinker {:main wiki-talk-shrinker.core
                        :uberjar-name "shrinker.jar"}})
