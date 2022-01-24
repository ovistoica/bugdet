(ns budget.app
  (:require [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [budget.db]
            [budget.transactions]
            [budget.nav :refer [start-router!]]
            [budget.components :refer [transaction-form application-shell
                                       transactions-table spending-chart]]))



(defn home-page []
  [:<>
   [transaction-form]
   [transactions-table]])


(defn reports-page []
  [:div {:class "w-full h-[38rem] mt-8 bg-white p-8 rounded"}
   [spending-chart]])


(defn pages [page-name]
  (case page-name
    :home [home-page]
    :reports [reports-page]
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
