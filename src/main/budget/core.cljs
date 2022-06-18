(ns budget.core
  (:require [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [budget.db]
            [budget.transactions]
            [budget.nav :refer [start-router!]]
            [budget.components :refer
             [transaction-form application-shell transactions-table
              spending-chart gauge-widget]]))



(defn home-page
  []
  [:<> [transaction-form] [transactions-table]])


(defn invoices-page
  []
  [:div.container
   [:p "Hello invoices"]])

(defn reports-page
  []
  (let [spending-limit @(rf/subscribe [:reports/spending-limit])]
    [:div.bg-white.p-8.m-2.w-full
     [:div.flex.flex-col.w-full.mt-2.items-center.justify-center
      [:h3 {:class "text-xl leading-6 font-medium text-gray-900"}
       "Spending in the current month"]
      [:p {:class "mt-1 max-w-2xl text-sm text-gray-500"}
       (str "Spending limit is set at: ₴" spending-limit " UAH")]
      [gauge-widget]]
     [:div {:class "w-full h-[38rem] mt-8 bg-white p-8 rounded"}
      [:h2.text-xl.font-semibold.text-gray-800.mb-4.text-center
       "Spending by month"] [spending-chart]]]))


(defn pages
  [page-name]
  (case page-name
    :home [home-page]
    :reports [reports-page]
    :invoices [invoices-page]
    [home-page]))


(defn app
  []
  (let [active-page @(rf/subscribe [:nav/active-page])]
    [application-shell [pages active-page]]))


(defn get-app-element
  []
  (gdom/getElement "app"))

(defn mount
  [el]
  (rdom/render [app] el))

(defn mount-app-element
  []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app"
;; element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload
  []
  (mount-app-element))
;; optionally touch your app-state to force rerendering depending on
;; your application


;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start
  []
  (start-router!)
  (rf/dispatch-sync [:app/initialize-db])
  (mount-app-element))

(defn init
  []
  (js/console.log "init")
  (start))
