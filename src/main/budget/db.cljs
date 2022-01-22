(ns budget.db
  (:require [re-frame.core :as rf]))



(def initial-db {:transactions
                 {:tr-01FSY64WBT0FGVDN9ABPHH5YWG
                  {:id        :tr-01FSY64WBT0FGVDN9ABPHH5YWG
                   :recipient "Jane Cooper"
                   :amount    2500
                   :time      "Fri Jan 21 2022 13:27:46"}}})



(rf/reg-event-fx
  :app/initialize-db
  ;[(rf/inject-cofx :local-store-user)]
  (fn [_ _]
    {:db initial-db}))


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
    (vals (:transactions db))))

(rf/reg-sub
  :transactions/total-spending)


(rf/reg-sub
  :gauge/percent
  :<- [:transactions/all])
