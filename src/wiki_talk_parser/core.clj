(ns wiki-talk-parser.core
  (:gen-class)
  (:require [clojure.data.xml :as xml]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]))

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
       (#(str/split % #":|/"))
       (second)))

; get Wikipedia UID via an API call
(defn get-user-id [user-name lang]
  (let [url (str "https://" lang ".wikipedia.org/w/api.php?action=query&list=users&format=json&ususers=" user-name)]
    (try
      (->> url
           (client/get)
           (:body)
           (#(json/read-str % :key-fn keyword))
           (:query)
           (:users)
           (first)
           (:userid))
      (catch Exception e nil))))  ; when something went wrong

(defn get-contributor-ids [page]
  (let [revisions (map :content (filter #(= :revision (:tag %)) page))
        contributors (map (partial some #(when (= :contributor (:tag %)) (:content %))) revisions)]
    (for [contributor contributors]
      (first (:content (second contributor))))))  ; IDs are always in the second place in <contributor>

(defn process-page [page lang]
  (let [user-id (get-user-id (get-user-name page) lang)]
    (when-not (nil? user-id)  ; ignore "User_talk" pages of IP users or whatever wrong
      (doseq [contributor-id (get-contributor-ids page) :when (not (nil? contributor-id))]  ; filter out IP contributors
        (println (str contributor-id "\t" user-id))))))  ; have to use "\t" as delimiter, ffs

(defn -main
  "Where everything starts happening."
  [& args]
  (let [input-file (io/reader (first args))
        lang (second args)]   ; language of Wikipedia
    (doseq [p (filter-page input-file)]
      (process-page p lang))))
