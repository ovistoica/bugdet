(ns budget.transactions
  (:require [cljs-time.coerce :refer [to-date-time]]
            [cljs-time.core :refer [month year]]
            [cljs-time.format :as format :refer [formatter]]
            [re-frame.core :as rf]))


(defn year-month-format
  "This format is used as ordering key for months
   as it is easy to compare
   Exampple: yyyyMM: January, 2021 -> 202201"
  [dt]
  (format/unparse (formatter "yyyyMM") dt))

(defn month-name
  "Short month name. Used to display month in graphs"
  [dt]
  (format/unparse (formatter "MMM") dt))


(rf/reg-event-db
  :transactions/add-transaction
  (fn [db [_ {:keys [amount recipient time id]}]]
    (assoc-in db [:transactions id] {:id        id
                                     :recipient recipient
                                     :amount    amount
                                     :time      time})))
(rf/reg-sub
  :transactions/all
  (fn [db _]
    ; return sorted by date entries
    (sort #(compare (:id %2) (:id %1))
          (vals (:transactions db)))))

(rf/reg-sub
  :transactions/total-spending
  :<- [:transactions/all]
  (fn [transactions [_ args]]
    (->> transactions
         (filter (fn [transaction]
                   (let [dt-obj (to-date-time (:time transaction))
                         t-month (month dt-obj)
                         t-year (year dt-obj)]
                     (and (= (:month args) t-month)
                          (= (:year args) t-year)))))
         (map :amount)
         (reduce +))))

(rf/reg-sub
  :transactions/spending-by-month
  :<- [:transactions/all]
  (fn [transactions _]
    (->> transactions
         ; Create a map with "yyyyMM" (year month) as key
         ; which will keep the total transaction amount for that month
         (reduce (fn
                   [month-map {:keys [time amount]}]
                   (let [dtobj (to-date-time time)
                         map-key (year-month-format dtobj)
                         month-map-val (or (get month-map map-key)
                                           {:name (str (month-name dtobj) "-" (year dtobj))})]
                     (assoc month-map map-key (update month-map-val
                                                      :total + amount)))) {})
         ; Sort based on the yyyyMM key
         (seq)
         (sort #(compare (first %1) (first %2)))
         ; get only values
         (map last))))

