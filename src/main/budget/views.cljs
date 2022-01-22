(ns budget.views
  (:require [clojure.string :as string]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [budget.helpers :refer [transaction-id]]))

(defn event-value [^js/Event e] (-> e .-target .-value))

;;;;;;;;;;;;;;;;;; Buttons ;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def default-button-style "bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded")

(defn button [props title]
  [:button (merge {:class default-button-style} props) title])

;;;;;;;;;;;; Transactions Table ;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn th [name]
  [:th.px-6.py-3.text-left.text-xs.font-medium.text-gray-500.uppercase.tracking-wider {:scope "col"}
   (string/upper-case name)])

(defn thead []
  [:thead.bg-gray-50
   [:tr
    [th "From"]
    [th "To"]
    [th "Amount"]
    [th "Status"]]])


(defn name-col [name]
  [:td.px-6.py-4.whitespace-nowrap
   [:div.flex.items-center
    [:div.text-sm.font-medium.text-gray-700 name]]])

(defn status-col []
  [:td.px-6.py-4.whitespace-nowrap
   [:span.px-2.inline-flex.text-xs.leading-5.font-semibold.rounded-full.bg-green-100.text-green-800 "Active"]])

(defn amount-col [amount]
  [:td.px-6.py-4.whitespace-nowrap.text-sm.font-medium.text-gray-900.underline (str amount " ₴")])

(defn table-container [children]
  [:div.flex.flex-col
   [:div.-my-2.overflow-x-auto.sm:-mx-6.lg:-mx-8
    [:div.py-2.align-middle.inline-block.min-w-full.sm:px-6.lg:px-8
     [:div.shadow.overflow-hidden.border-b.border-gray-200.sm:rounded-lg children]]]])

(defn transaction-table []
  (let [transactions @(rf/subscribe [:transactions/all])]
    [table-container
     [:table.min-w-full.divide-y.divide-gray-200
      [thead]
      [:tbody.bg-white.divide-y.divide-gray-200h
       (map (fn [{:keys [recipient amount id]}]
              [:tr {:key id}
               [name-col "Ovidiu"]
               [name-col recipient]
               [amount-col amount]
               [status-col]]) transactions)]]]))


;;;;;;;;;;;;;;;;;; Forms ;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn form-group
  [{:keys [id label type values placeholder class]}]
  [:div
   [:label.block.text-gray-700.text-sm.font-bold.mb-2 {:for id}
    label
    [:input
     {:class       (str (when class class)
                        "shadow appearance-none border rounded w-full py-2 px-3 "
                        "text-gray-700 leading-tight focus:outline-none focus:shadow-outline ")
      :id          id
      :type        type
      :placeholder placeholder
      :value       (id @values)
      :on-change   #(swap! values assoc id (event-value %))}]]])


(defn new-transaction-form []
  (let [initial-values {:amount 0 :recipient ""}
        values (r/atom initial-values)
        save (fn [{:keys [amount recipient]}]
               (rf/dispatch [:transactions/add-transaction
                             {:recipient (string/trim recipient)
                              :amount    (js/parseInt amount 10)
                              :id        (transaction-id)
                              :time      (js/Date.)}])
               (reset! values initial-values))]
    (fn []
      [:div.grid.grid-cols-2.gap-2.py-4
       [form-group {:id          :recipient
                    :label       "To"
                    :placeholder "Who will receive the money"
                    :type        "text"
                    :values      values}]
       [:div.w-24
        [form-group {:id     :amount
                     :label  "Amount"
                     :type   "number"
                     :values values}]]
       [:div.w-25
        [button {:on-click #(save @values)} "Send"]]])))

