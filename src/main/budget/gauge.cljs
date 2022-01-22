(ns budget.gauge
  (:require ["react-gauge-chart" :default GaugeChart]
            [reagent.core :as r]
            [budget.styles :refer [colors]]))

(def green-spending-limit 18000)
(def yellow-spending-limit 35000)

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
  [gauge {:id           gauge-id
          :text-color   (:grey-800 colors)
          :needle-color (:blue-700 colors)}])



