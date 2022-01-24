(ns budget.app
  (:require [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [budget.db]
            [budget.nav :refer [start-router!]]
            [budget.components :refer [price-input name-input button-primary application-shell
                                       transactions-table example-chart]]))

(defn transaction-form []
  [:div.pb-6
   [:h3.text-xl.font-medium.text-gray-700 "New transaction"]
   [:div.grid.grid-cols-2.gap-2.mt-4.max-w-6xl
    [name-input {:id "transaction" :label "To" :on-change #(prn %)}]
    [price-input {:id "amount" :label "Amount" :on-change #(prn %)}]]
   [:div.mt-6
    [button-primary {:size :md
                     :text "Create transaction"}]]])


(defn home-page []
  [:<>
   [transaction-form]
   [transactions-table]])


(defn graph-page []
  [example-chart])

(defn pages [page-name]
  (case page-name
    :home [home-page]
    :graph [graph-page]
    [home-page]))


(defn app
  []
  (let [active-page @(rf/subscribe [:nav/active-page])]
    [application-shell
     [pages active-page]]))


(defn get-app-element []
  (gdom/getElement "app"))

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
  (start-router!)
  (rf/dispatch-sync [:app/initialize-db])
  (mount-app-element))

(defn init []
  (js/console.log "init")
  (start))
