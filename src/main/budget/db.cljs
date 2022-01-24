(ns budget.db
  (:require [re-frame.core :as rf]
            [budget.helpers :as h]))



(def initial-db {:app          {:show-sidebar? false}
                 :nav          {:active-page :home}
                 :transactions {:tr-01FQGJA76T9ABK80DBGTQT9Q42
                                {:id        :tr-01FQGJA76T9ABK80DBGTQT9Q42,
                                 :recipient "John",
                                 :amount    2500,
                                 :time      "2021-12-22T07:57:25.594-00:00"}

                                :tr-01FQK4PY6T5GC2JAK66FGXWT5X
                                {:id        :tr-01FQK4PY6T5GC2JAK66FGXWT5X,
                                 :recipient "John",
                                 :amount    2500,
                                 :time      "2021-12-23T07:57:25.594-00:00"}

                                :tr-01FT0C5JJTPG5XYQ9R3AKDJDAQ
                                {:recipient "Groceries"
                                 :amount    4000
                                 :id        :tr-01FT0C5JJTPG5XYQ9R3AKDJDAQ
                                 :time      "2022-01-22T07:49:16.507-00:00"}

                                :tr-01FQK4PY6TEAEAWV6P7Z0K0NWS
                                {:id        :tr-01FQK4PY6TEAEAWV6P7Z0K0NWS
                                 :recipient "John"
                                 :amount    2500
                                 :time      "2021-12-23T07:57:25.594-00:00"}

                                :tr-01FPPTB16T9TBSPJMQH8509CA5
                                {:id        :tr-01FPPTB16T9TBSPJMQH8509CA5
                                 :recipient "Rent"
                                 :amount    8000
                                 :time      "2021-12-12T07:57:25.594-00:00"}

                                :tr-01FKFTE96TS0NJQJBK4FFPNE7Q
                                {:id        :tr-01FKFTE96TS0NJQJBK4FFPNE7Q
                                 :recipient "Party"
                                 :amount    7512
                                 :time      "2021-11-02T07:57:25.594-00:00"}

                                :tr-01FKZ8TK6TXPAWNTZ7HYGT587W
                                {:id        :tr-01FKZ8TK6TXPAWNTZ7HYGT587W
                                 :recipient "Doctor",
                                 :amount    5500,
                                 :time      "2021-11-08T07:57:25.594-00:00"}}})


(def months {"January"   1
             "February"  2
             "March"     3
             "April"     4
             "May"       5
             "June"      6
             "July"      7
             "August"    8
             "September" 9
             "October"   10
             "November"  11
             "December"  12})


(rf/reg-event-fx
  :app/initialize-db
  (fn [_ _]
    {:db initial-db}))

(rf/reg-event-db
  :app/show-mobile-sidebar
  (fn [db [_ new-state]]
    (assoc-in db [:app :show-sidebar?] new-state)))

(rf/reg-event-db
  :app/toggle-mobile-sidebar
  (fn [db _]
    (let [new-state (not (get-in db [:app :show-sidebar?]))]
      (assoc-in db [:app :show-sidebar?] new-state))))

(rf/reg-sub
  :app/show-sidebar?
  (fn [db _]
    (get-in db [:app :show-sidebar?])))

(comment
  (rf/subscribe [:app/show-sidebar?])
  (rf/dispatch [:app/toggle-mobile-sidebar])
  (rf/dispatch [:app/show-mobile-sidebar true]))



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
  (fn [transactions [_ {:keys [month year]}]]
    (->> transactions
         (filter (fn [transaction]
                   (let [transaction-timestamp (:time transaction)
                         t-month (h/timestamp->month transaction-timestamp)
                         t-year (h/timestamp->year transaction-timestamp)]
                     (and (= month t-month)
                          (= year t-year)))))
         (map :amount)
         (reduce +))))


(comment
  (rf/clear-subscription-cache!)
  (rf/subscribe [:transactions/all])
  (rf/subscribe [:transactions/total-spending {:month 12 :year 2021}]))

