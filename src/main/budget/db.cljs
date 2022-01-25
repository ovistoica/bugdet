(ns budget.db
  (:require [re-frame.core :as rf]))



(def initial-db {:app          {:show-mobile-sidebar? false}

                 :nav          {:active-page :home}

                 ; Spending limits for the Gauge widget. Values are in UAH
                 :reports      {:spending-limit 30000}

                 ; Dummy transactions to have some data
                 :transactions {:tr-01FQGJA76T9ABK80DBGTQT9Q42 {:id        :tr-01FQGJA76T9ABK80DBGTQT9Q42,
                                                                :recipient "John",
                                                                :amount    2500,
                                                                :time      "2021-12-22T07:57:25.594-00:00"}

                                :tr-01FT0C5JJTPG5XYQ9R3AKDJDAQ {:recipient "Groceries"
                                                                :amount    4000
                                                                :id        :tr-01FT0C5JJTPG5XYQ9R3AKDJDAQ
                                                                :time      "2022-01-22T07:49:16.507-00:00"}

                                :tr-01FPPTB16T9TBSPJMQH8509CA5 {:id        :tr-01FPPTB16T9TBSPJMQH8509CA5
                                                                :recipient "Rent"
                                                                :amount    8000
                                                                :time      "2021-12-12T07:57:25.594-00:00"}

                                :tr-01FKFTE96TS0NJQJBK4FFPNE7Q {:id        :tr-01FKFTE96TS0NJQJBK4FFPNE7Q
                                                                :recipient "Party"
                                                                :amount    7512
                                                                :time      "2021-11-02T07:57:25.594-00:00"}

                                :tr-01FKZ8TK6TXPAWNTZ7HYGT587W {:id        :tr-01FKZ8TK6TXPAWNTZ7HYGT587W
                                                                :recipient "Doctor",
                                                                :amount    5500,
                                                                :time      "2021-11-08T07:57:25.594-00:00"}}})

(rf/reg-event-fx
  :app/initialize-db
  (fn [_ _]
    {:db initial-db}))

(rf/reg-event-db
  :app/show-mobile-sidebar
  (fn [db [_ new-state]]
    (assoc-in db [:app :show-mobile-sidebar?] new-state)))

(rf/reg-event-db
  :app/toggle-mobile-sidebar
  (fn [db _]
    (let [new-state (not (get-in db [:app :show-mobile-sidebar?]))]
      (assoc-in db [:app :show-mobile-sidebar?] new-state))))

(rf/reg-sub
  :app/show-mobile-sidebar?
  (fn [db _]
    (get-in db [:app :show-mobile-sidebar?])))

(comment
  (rf/subscribe [:app/show-sidebar?])
  (rf/dispatch [:app/toggle-mobile-sidebar])
  (rf/dispatch [:app/show-mobile-sidebar true]))


(comment
  (rf/clear-subscription-cache!)
  (rf/subscribe [:transactions/all])
  (rf/subscribe [:transactions/total-spending {:month 12 :year 2021}]))

