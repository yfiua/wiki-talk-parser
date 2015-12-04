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

(defn with-prefix? [line prefix]
  (let [length (count prefix)]
    (if (< (count line) length)
      false
      (= prefix (subs line 0 length)))))

; extract the value from "<xxx>value</xxx>"
(defn extract-value [line prefix]
  (let [length (count prefix)]
    (subs line length (- (count line) (inc length)))))

(defn parse [lines lang status title user-id]
  (when (seq lines)
    (let [line (str/trim (first lines))]
      (condp = status
        0   ; normal
        (if (with-prefix? line "<title>")
          (recur (rest lines) lang 1 (extract-value line "<title>") nil)
          (recur (rest lines) lang 0 nil nil))
        1   ; found a page
        (if (with-prefix? line "<ns>")
          (if (= "3" (extract-value line "<ns>"))
            (recur (rest lines) lang 2 nil (get-user-id (second (str/split title #":|/")) lang))
            (recur (rest lines) lang 0 nil nil))
          (if (= line "</page>")
            (recur (rest lines) lang 0 nil nil)          ; end of a page
            (recur (rest lines) lang 1 title nil)))
        2   ; found a "User_talk" page
        (if (nil? user-id)
          (recur (rest lines) lang 0 nil nil)            ; ignore IP user
          (if (= line "<contributor>")
            (recur (rest lines) lang 3 nil user-id)
            (if (= line "</page>")
              (recur (rest lines) lang 0 nil nil)         ; end of a page
              (recur (rest lines) lang 2 nil user-id))))
        3   ; found a contributor
        (if (with-prefix? line "<id>")
          (do
            (println (str (extract-value line "<id>") "\t" user-id)) ; have to use "\t" as delimiter, ffs
            (recur (rest lines) lang 2 nil user-id))
          (if (= line "</contributor>")
            (recur (rest lines) lang 2 nil user-id)     ; end of a contributor
            (recur (rest lines) lang 3 nil user-id)))))))


(defn -main
  "Where everything starts happening."
  [& args]
  (let [input-file (io/reader (first args))
        lang (second args)]   ; language of Wikipedia
    (parse (line-seq input-file) lang 0 nil nil)))
