(ns budget.components
  (:require [budget.helpers :refer [classes transaction-id]]
            [budget.nav :as nav]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [cljs-time.format :as format :refer [formatter]]
            [cljs-time.coerce :refer [from-date]]
            [budget.styles :refer [colors]]
            [clojure.string :as string]
            ["@headlessui/react" :refer [Transition Dialog]]
            ["@heroicons/react/outline" :refer [XIcon HomeIcon ChartBarIcon MenuAlt2Icon BellIcon]]
            ["@heroicons/react/solid" :refer [ExclamationCircleIcon]]
            ["react" :refer [Fragment]]
            ["recharts" :refer [BarChart Bar XAxis YAxis Tooltip Legend ResponsiveContainer]]
            ["react-gauge-chart" :default GaugeChart]))

(def chart-responsive-container (r/adapt-react-class ResponsiveContainer))
(def bar-chart (r/adapt-react-class BarChart))
(def bar (r/adapt-react-class Bar))
(def x-axis (r/adapt-react-class XAxis))
(def y-axis (r/adapt-react-class YAxis))
(def tooltip (r/adapt-react-class Tooltip))
(def legend (r/adapt-react-class Legend))

(def dialog (r/adapt-react-class Dialog))
(def transition-child
  "Used to coordinate multiple transitions based on the same event.
  Needs to be nested inside a transition-root element."
  (r/adapt-react-class (.-Child Transition)))
(def transition-root
  (r/adapt-react-class (.-Root Transition)))
(def dialog-overlay (r/adapt-react-class (.-Overlay Dialog)))
(def fragment Fragment)


;;;;;;;;;;;;;;;;;; icons ;;;;;;;;;;;;;;;;;;;;;;
(def x-icon (r/adapt-react-class XIcon))
(def home-icon (r/adapt-react-class HomeIcon))
(def chart-icon (r/adapt-react-class ChartBarIcon))
(def menu-icon (r/adapt-react-class MenuAlt2Icon))
(def bell-icon (r/adapt-react-class BellIcon))
(def error-icon (r/adapt-react-class ExclamationCircleIcon))

(defn payment-icon []
  [:svg.flex-shrink-0.h-5.w-5.text-gray-400.group-hover:text-gray-500
   {:xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor" :aria-hidden "true"}
   [:path {:fill-rule "evenodd"
           :d         "M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z"
           :clip-rule "evenodd"}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;   Nav ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def navigation-elements [{:id       :home
                           :name     "Home",
                           :href     (nav/path-for :home)
                           :icon     home-icon
                           :dispatch #(rf/dispatch [:nav/set-active-page :home])}
                          {:id       :reports
                           :name     "Reports"
                           :href     (nav/path-for :reports)
                           :icon     chart-icon
                           :dispatch #(rf/dispatch [:nav/set-active-page :reports])}])


(defn mobile-sidebar []
  (let [rf-open? @(rf/subscribe [:app/show-mobile-sidebar?])
        open? (if (boolean? rf-open?)
                rf-open?
                false)]

    [transition-root {:show open?
                      :as   fragment}
     [dialog {:as       "div" :class "fixed inset-0 flex z-40 md:hidden"
              :on-close #(rf/dispatch [:app/show-mobile-sidebar false])}
      [transition-child {:as        fragment
                         :enter     "transition-opacity ease-linear duration-300"
                         :enterFrom "opacity-0"
                         :enterTo   "opacity-100"
                         :leave     "transition-opacity ease-linear duration-300"
                         :leaveFrom "opacity-100"
                         :leaveTo   "opacity-0"}
       [dialog-overlay {:class "fixed inset-0 bg-gray-600 bg-opacity-75"}]]
      [transition-child {:as        fragment
                         :enter     "transition ease-in-out duration-300 transform"
                         :enterFrom "-translate-x-full"
                         :enterTo   "translate-x-0"
                         :leave     "transition ease-in-out duration-300 transform"
                         :leaveFrom "translate-x-0"
                         :leaveTo   "-translate-x-full"}

       [:div {:class "relative flex-1 flex flex-col max-w-xs w-full pt-5 pb-4 bg-white"}
        [transition-child
         {:as        fragment
          :enter     "ease-in-out duration-300"
          :enterFrom "opacity-0"
          :enterTo   "opacity-100"
          :leave     "ease-in-out duration-300"
          :leaveFrom "opacity-100"
          :leaveTo   "opacity-0"}


         [:div.absolute.top-0.right-0.-mr-12.pt-2
          [:button {:type     :button
                    :class
                    (classes "ml-1 flex items-center justify-center h-10 w-10 rounded-full"
                             "focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white")
                    :on-click #(rf/dispatch [:app/show-mobile-sidebar false])}
           [:span.sr-only "Close sidebar"]
           [x-icon {:class "h-6 w-6 text-white" :aria-hidden true}]]]]
        [:div.flex-shrink-0.flex.items-center.px-4
         [:img.h-8.w-auto
          {:src "https://designvote-storage.fra1.cdn.digitaloceanspaces.com/logo-transaction.png"
           :alt "Transaction Manager"}]]
        [:div.mt-5.flex-1.h-0.overflow-y-auto
         [:nav.px-2.space-y-1
          (for [{:keys [icon current href name]} navigation-elements]
            ^{:key (str "mobile-nav " name)}
            [:a {:href  href
                 :class (classes "group flex items-center px-2 py-2 text-base font-medium rounded-md"
                                 (if current
                                   "bg-gray-100 text-gray-900"
                                   "text-gray-600 hover:bg-gray-50 hover:text-gray-900"))}
             [icon {:class (classes "mr-4 flex-shrink-0 h-6 w-6"
                                    (if current "text-gray-500"
                                                "text-gray-400 group-hover:text-gray-500"))}] name])]]]]
      ; dummy element to force sidebar to shrink to fit close icon
      [:div.flex-shrink-0.w-14 {:aria-hidden true}]]]))



(defn desktop-sidebar []
  (let [active-page @(rf/subscribe [:nav/active-page])]
    [:div {:class "hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0"}
     [:div {:class (classes "flex flex-col flex-grow border-r border-gray-200"
                            "pt-5 bg-white overflow-y-auto")}
      [:div {:class "flex items-center flex-shrink-0 px-4"}
       [:img {:class "h-8 w-auto"
              :src   "https://designvote-storage.fra1.cdn.digitaloceanspaces.com/logo-transaction.png"
              :alt   "Transaction Manager"}]]
      [:div {:class "mt-5 flex-grow flex flex-col"}
       [:nav {:class "flex-1 px-2 pb-4 space-y-1"}
        (for [{:keys [icon href name id dispatch]} navigation-elements
              :let [active? (= active-page id)]]
          ^{:key (str "mobile-nav " name)}
          [:a {:href     href
               :on-click #(dispatch)
               :class    (classes "group flex items-center px-2 py-2 text-sm font-medium rounded-md"
                                  (if active? "bg-gray-100 text-gray-900" "text-gray-600 hover:bg-gray-50 hover:text-gray-900"))}
           [icon {:class       (classes "mr-3 flex-shrink-0 h-6 w-6"
                                        (if active? "text-gray-500" "text-gray-400 group-hover:text-gray-500"))
                  :aria-hidden true}] name])]]]]))


(defn application-top-bar []
  [:div.sticky.top-0.flex-shrink-0.flex.h-16.bg-white.shadow
   [:button {:type     "button"
             :class    (classes "px-4 border-r border-gray-200 text-gray-500 focus:outline-none"
                                "focus:ring-2 focus:ring-inset focus:ring-indigo-500 md:hidden")
             :on-click #(rf/dispatch [:app/toggle-mobile-sidebar])}
    [:alt.sr-only "Open sidebar"]
    [menu-icon {:class "h-6 w-6" :aria-hidden true}]]
   [:div.flex-1.px-4.flex.justify-between
    [:div.flex-1.flex]
    [:div.ml-4.flex.items-center.md:ml-6
     [:button {:type  :button
               :class (classes "bg-white p-1 rounded-full text-gray-400 hover:text-gray-500"
                               "focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500")}
      [:span.sr-only "View notifications"]
      [bell-icon {:class "h-6 w-6" :aria-hidden true}]]]]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;    Layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn application-shell
  "Shell containing side bar used on all application pages"
  [& children]
  (let [title @(rf/subscribe [:nav/page-title])]
    [:div
     [mobile-sidebar]
     [desktop-sidebar]
     [:div {:className "md:pl-64 flex flex-col flex-1"}
      [application-top-bar]
      [:main.flex-1
       [:div.py-6
        [:div.max-w-7xl.mx-auto.px-4.sm:px-6.md:px-8
         [:h1.text-2xl.font-semibold.text-gray-900 title]]
        [:div.max-w-7xl.mx-auto.px-4.sm:px-6.md:px-8
         children]]]]]))


(defn container [& children]
  [:div {:class (classes "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8")}
   children])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Forms ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn event-value [^js/Event e] (-> e .-target .-value))

(def error-input-style "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500")

(defn price-input [{:keys [id label name on-change value class error]}]
  [:div
   [:label.block.text-sm.font-medium.text-gray-700 {:for id} label]
   [:div.mt-1.relative.rounded-md.shadow-sm
    [:div.absolute.inset-y-0.left-0.pl-3.flex.items-center.pointer-events-none
     [:span.text-gray-500.sm:text-sm "₴"]]
    [:input {:id               id
             :type             "text"
             :name             name
             :placeholder      "0.00"
             :aria-describedby (if error "amount-error" "price-currency")
             :on-change        on-change
             :value            value
             :class            (classes (if error
                                          error-input-style
                                          "focus:ring-indigo-500 focus:border-indigo-500 border-gray-300")
                                        "block w-full  rounded-md pl-7 pr-12 sm:text-sm"
                                        (when class class))}]
    [:div.absolute.inset-y-0.right-0.pr-3.flex.items-center.pointer-events-none
     (if error
       [error-icon {:class "h-5 w-5 text-red-500" :aria-hidden true}]
       [:span#price-currency.text-gray-500.sm:text-sm "UAH"])]
    (when error [:p#amount-error {:className "absolute -bottom-8 text-sm text-red-600"} error])]])

(defn name-input [{:keys [id label name placeholder on-change value class]}]
  [:div
   [:label.block.text-sm.font-medium.text-gray-700 {:for id} label]
   [:div.mt-1.relative.rounded-md.shadow-sm
    [:input {:id          id
             :type        "text"
             :name        name
             :placeholder (or placeholder "Jane Doe")
             :on-change   on-change
             :value       value
             :class       (classes "focus:ring-indigo-500 focus:border-indigo-500 block w-full"
                                   "border-gray-300 rounded-md pl-7 pr-12 sm:text-sm"
                                   (when class class))}]]])



(defn button-size-classes [size]
  (case size
    :xs "px-2.5 py-1.5 text-xs rounded"
    :sm "px-3 py-2 text-sm rounded-md"
    :md "px-4 py-2 text-sm rounded-md"
    :lg "px-4 py-2 text-base rounded-md"
    :xl "px-6 py-3 text-base rounded-md"))

(def disabled-style "cursor-not-allowed opacity-50")

(defn button-primary [{:keys [size text on-click class disabled?]}]
  (prn disabled?)
  (let [size-classes (button-size-classes size)]
    [:button {:class    (classes "inline-flex items-center border border-transparent font-medium "
                                 "focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                                 "bg-indigo-600 hover:bg-indigo-700 shadow-sm text-white"
                                 size-classes
                                 (when disabled? disabled-style)
                                 (when class class))
              :type     :button
              :on-click #(when-not disabled?
                           (on-click))} text]))


(defn transaction-form
  "Form used to add a new transaction"
  []
  (let [initial-values {:amount nil :recipient ""}
        values (r/atom initial-values)
        save (fn [{:keys [amount recipient]}]
               (rf/dispatch [:transactions/add-transaction
                             {:recipient (string/trim recipient)
                              :amount    (js/parseInt amount 10)
                              :id        (transaction-id)
                              :time      (str (js/Date.))}])
               (reset! values initial-values))]
    (fn []
      (let [price-value (js/parseInt (:amount @values))
            input-error? (and (not (nil? (:amount @values)))
                              (js/isNaN price-value))
            button-disabled? (or input-error?
                                 (not (pos-int? price-value))
                                 (zero? (count (:recipient @values))))]
        [:div.pb-6
         [:div.grid.grid-cols-2.gap-2.mt-4.max-w-6xl
          [name-input {:id        :recipient
                       :label     "To"
                       :value     (:recipient @values)
                       :on-change #(swap! values assoc
                                          :recipient (event-value %))}]
          [price-input {:id        "amount"
                        :label     "Amount"
                        :value     (:amount @values)
                        :error     (when input-error? "Please enter a valid amount")
                        :on-change #(swap! values assoc
                                           :amount (event-value %))}]]
         [:div.mt-6
          [button-primary {:size      :md
                           :text      "Create transaction"
                           :on-click  #(save @values)
                           :disabled? button-disabled?}]]]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Table ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn table-container [& children]
  [:div.max-w-6xl
   [:div.flex.flex-col.mt-2
    [:div.align-middle.min-w-full.overflow-x-auto.shadow.overflow-hidden.sm:rounded-lg
     children]]])

(defn table
  "General styled table"
  [{:keys [head rows]}]
  [table-container
   [:table.min-w-full.divide-y.divide-gray-200
    head
    [:tbody.bg-white.divide-y.divide-gray-200
     rows]]])

(defn transactions-table-head []
  [:thead
   [:tr
    [:th.px-6.py-3.bg-gray-50.text-left.text-xs.font-medium.text-gray-500.uppercase.tracking-wider "Transaction"]
    [:th.px-6.py-3.bg-gray-50.text-right.text-xs.font-medium.text-gray-500.uppercase.tracking-wider "Amount"]
    [:th.hidden.px-6.py-3.bg-gray-50.text-left.text-xs.font-medium.text-gray-500.uppercase.tracking-wider.md:block "Status"]
    [:th.px-6.py-3.bg-gray-50.text-right.text-xs.font-medium.text-gray-500.uppercase.tracking-wider "Date"]]])


(defn recipient-column [recipient]
  [:td.max-w-0.w-full.px-6.py-4.whitespace-nowrap.text-sm.text-gray-900
   [:div.flex
    [:a.group.inline-flex.space-x-2.truncate.text-sm {:href "#"}
     [payment-icon]
     [:p.text-gray-500.truncate.group-hover:text-gray-900 (str "Payment to " recipient)]]]])

(defn amount-col [amount]
  [:td.px-6.py-4.text-right.whitespace-nowrap.text-sm.text-gray-500
   [:span.text-gray-900.font-medium (str "₴" amount)] " UAH"])

(defn status-col []
  [:td.hidden.px-6.py-4.whitespace-nowrap.text-sm.text-gray-500.md:block
   [:span.inline-flex.items-center.px-2.5.py-0.5.rounded-full.text-xs.font-medium.bg-green-100.text-green-800.capitalize "success"]])

(defn date-col [timestamp]
  (let [dt (from-date (js/Date. timestamp))
        pretty-format (format/unparse (formatter "MMMM dd, yyyy") dt)
        datetime (format/unparse (formatter "yyyy-MM-dd") dt)]
    [:td.px-6.py-4.text-right.whitespace-nowrap.text-sm.text-gray-500
     [:time {:dateTime datetime} pretty-format]]))

(defn transaction-table-row [{:keys [recipient amount time]}]
  [:tr.bg-white
   [recipient-column recipient]
   [amount-col amount]
   [status-col]
   [date-col time]])


(defn transactions-table []
  (let [transactions @(rf/subscribe [:transactions/all])]
    [table {:head [transactions-table-head]
            :rows (for [t transactions]
                    ^{:key (:id t)}
                    [transaction-table-row t])}]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Charts ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn spending-chart
  "A chart that shows total spending by month."
  []
  (let [spending-by-month @(rf/subscribe [:transactions/spending-by-month])]
    [chart-responsive-container {:width "100%" :height "100%"}
     [bar-chart {:width  500 :height 300
                 :data   spending-by-month
                 :margin {:top 5 :right 30 :left 20 :bottom 5}}
      [x-axis {:dataKey "name"}]
      [y-axis {:unit "₴"}]
      [tooltip {:name "total spending" :unit "₴"}]
      [legend]
      [bar {:dataKey "total" :fill "#8884d8"}]]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Gauge ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def gauge
  "Gauge Widget:
  Props:
   :nr-of-levels - number  - Number of levers on the gauge
   :percent - number - The percent the needle is at
   :arc-width - number
   :colors - array of string color hexes to display on the arc left to right
   :corner radius - number
   :arc-padding - number
   :needle-color - color hex string
   :style - style map
   :class - applied to the div container
   :margin-in-percent - number
   :text-color - color hex string
   :animate - bool
   :format-text-value - function. Example: (fn [text] (str text \"%\"))
  "
  (r/adapt-react-class GaugeChart))

(def gauge-id "budget-manager-gauge-id")

(defn gauge-widget []
  (let [percent @(rf/subscribe [:reports/spending-percent])]
    [gauge {:id           gauge-id
            :style        {:height 100 :width 300 :margin-bottom 8}
            :percent      percent
            :text-color   (:grey-800 colors)
            :needle-color (:indigo-400 colors)
            :colors       ((juxt :green-300 :yellow-300 :red-300) colors)}]))
