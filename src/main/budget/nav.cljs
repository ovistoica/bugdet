(ns budget.nav
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]))

(def routes ["/" {"" :home "reports" :reports}])

(def history
  (let [dispatch #(rf/dispatch [:nav/route-changed %])
        match #(bidi/match-route routes %)]
    (pushy/pushy dispatch match)))


(defn start-router!
  []
  (pushy/start! history))

(def path-for
  "Get the path for the screen-id or an error if it doesn't exist.
  Example:
  For route :profile => \"profile\""
  (partial bidi/path-for routes))


(defn set-token!
  [token]
  (pushy/set-token! history token))

(rf/reg-fx
  :nav/navigate-to
  (fn [{:keys [path]}]
    (set-token! path)))

(rf/reg-event-db
  :nav/set-active-page
  (fn [db [_ active-page]]
    (assoc-in db [:nav :active-page] active-page)))

(rf/reg-event-db
  :nav/route-changed
  (fn [db [_ {:keys [handler]}]]
    (assoc-in db [:nav :active-page] handler)))

(rf/reg-sub
  :nav/active-page
  (fn [db _]
    (get-in db [:nav :active-page])))

(rf/reg-sub
  :nav/page-title
  :<-
  [:nav/active-page]
  (fn [active-page _]
    (case active-page
      :home "Transactions"
      :reports "Spendings"
      "Transactions")))


(comment
  (rf/dispatch [:nav/set-active-page :graph])
  (rf/subscribe [:nav/active-page]))
