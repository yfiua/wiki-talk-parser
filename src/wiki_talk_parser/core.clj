(ns wiki-talk-parser.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clj-http.client :as client]))

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
              (recur (rest lines) lang 0 nil nil)        ; end of a page
              (recur (rest lines) lang 2 nil user-id))))
        3   ; found a contributor
        (if (with-prefix? line "<id>")
          (do
            (println (str (extract-value line "<id>") "\t" user-id))
            (recur (rest lines) lang 2 nil user-id))
          (if (= line "</contributor>")
            (recur (rest lines) lang 2 nil user-id)      ; end of a contributor
            (recur (rest lines) lang 3 nil user-id)))))))

; main
(defn -main
  "Where everything starts happening."
  [& args]
  (let [input-file-name (first args)
        lang (second args)]   ; language of Wikipedia
    (with-open [input-file (io/reader input-file-name)]
      (parse (line-seq input-file) lang 0 nil nil))))
