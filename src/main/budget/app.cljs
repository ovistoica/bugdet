(ns budget.app
  (:require [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [budget.views :as v]
            [budget.gauge :refer [gauge-widget]]
            [budget.chart :refer [example-chart]]
            [re-frame.core :as rf]
            [budget.db]))

(defn get-app-element []
  (gdom/getElement "app"))

(defn budget-manager []
  (let [x (rf/subscribe [:transactions/total-spending])]
    [:div.container.mx-auto
     [:div.w-72.h-72 [example-chart]]
     [:h1.text-xl.font-bold.py-4.text-gray-700
      "Transaction manager"]
     [v/new-transaction-form]
     [v/transaction-table]
     [:div {:class "w-60"}
      [gauge-widget]]]))

(defn app
  []
  [budget-manager])

(defn mount [el]
  (rdom/render [app] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))
;; optionally touch your app-state to force rerendering depending on
;; your application


;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (rf/dispatch-sync [:app/initialize-db])
  (mount-app-element))

(defn init []
  (js/console.log "init")
  (start))
