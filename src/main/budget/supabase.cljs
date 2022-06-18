(ns budget.supabase
  (:require ["@supabase/supabase-js" :refer [createClient]]
            [promesa.core :as p]))


(def supabase-url "https://uaokntvloogearcqdahn.supabase.co")
(def supabase-key "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVhb2tudHZsb29nZWFyY3FkYWhuIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NTU1NjQzNjcsImV4cCI6MTk3MTE0MDM2N30.mxhKD308Oe6hn7Sc4AK4H0RnN3WKAFxCjzVk5oaGhN0")

(def supabase-client (atom nil))

(defn create-client
  []
  (reset! supabase-client (createClient supabase-url supabase-key)))

(defn sign-in
  [email password]
  (let [js-sign-in (.-signIn (.-auth @supabase-client))]
    (p/let [res (js-sign-in email password)]
      (js->clj res))))


(comment
  (-> (sign-in "test@test.com" "test")
      (.then (fn [res]
               (println res)
               (println (.-token res))
               (println (.-token res))))))








