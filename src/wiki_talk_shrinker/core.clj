(ns wiki-talk-shrinker.core
  (:gen-class)
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn read-to-map [lines m]
  (if (seq lines)
    (let [line (first lines)
          s (str/split line #"\t")]
      (recur
        (rest lines)
        (if (= (first s) (second s))
          m
          (merge-with set/union m {(second s) #{(first s)}}))))   ; the target node is the key
    m))

(defn print-map [m]
  (doseq [e m
          v (val e)]
    (println (str v "\t" (key e)))))   ; the key is the target node
    
; main
(defn -main
  "Where everything starts happening."
  [& args]
  (let [input-file (io/reader (first args))]
    (print-map
      (read-to-map (line-seq input-file) {}))))
