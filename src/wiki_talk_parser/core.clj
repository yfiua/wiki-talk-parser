(ns wiki-talk-parser.core
  (:gen-class)
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as str]))

; filter all "User_talk" pages
(defn filter-page [rdr]
  (filter (fn [p] (= "3" (some #(when (= :ns (:tag %))
                                  (first (:content %))) p)))
          (map :content (filter #(= :page (:tag %))
                                (:content (xml/parse rdr))))))

; get the user's name of a given "User_talk" page
(defn get-user-name [page]
  (->> page
       (some #(when (= :title (:tag %)) (first (:content %))))
       (#(str/split % #":"))
       (last)))

(defn get-contributor-ids [page]
  (let [revisions (map :content (filter #(= :revision (:tag %)) page))
        contributors (map (partial some #(when (= :contributor (:tag %)) (:content %))) revisions)]
    (for [contributor contributors]
      (first (:content (second contributor))))))  ; IDs are always in the second place in <contributor>

(defn process-page [page]
  (let [user-name (get-user-name page)]
    (doseq [contributor-id (get-contributor-ids page) :when (not (nil? contributor-id))]  ; filter out IP contributors
      (println (str user-name "\t" contributor-id)))))  ; have to use "\t" as delimiter, ffs

(defn -main
  "Where everything starts happening."
  [& args]
  (let [input-file (io/reader (first args))]
    (doseq [p (filter-page input-file)]
      (process-page p))))
