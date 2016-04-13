(ns wiki-user-grouper.core
  (:gen-class)
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def role-list-1
  ["bot"])

(def role-list-2
  ["sysop"
   "bureaucrat"
   "founder"])

(defn get-role [role-str]
  (cond
    (.contains role-str "bot")
    1
    (.contains role-list-2 role-str)
    2
    :else
    0))
;   role-str))


(defn read-to-map [users m]
  (if (seq users)
    (let [user (first users)
          s (str/split user #"\(|,|\'")
          user-id (second s)
          role (get-role (nth s 3))]
      (recur
        (rest users)
        (if (or (nil? (m user-id)) (< (m user-id) role))
;       (if (or (nil? (m user-id))); (< (m user-id) role))   ;debeg
          (merge m {user-id role})
          m)))
    m))

(defn print-map [m]
  (doseq [e m]
    (println (str (key e) "\t" (val e)))))
 
(defn parse [line]
  (when (.contains line "INSERT INTO")
    (let [content (last (str/split line #"\s"))
          users (str/split content #"\),")]
      (print-map (read-to-map users {})))))

; main
(defn -main
  "Where everything starts happening."
  [& args]
  (let [input-file (io/reader (first args))]
    (doseq [line (line-seq input-file)]
      (parse line))))
