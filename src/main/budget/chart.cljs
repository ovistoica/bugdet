(ns budget.chart
  (:require ["recharts" :refer [LineChart Line XAxis YAxis Tooltip Legend ResponsiveContainer CartesianGrid]]
            [reagent.core :as r]))

(def chart-responsive-container (r/adapt-react-class ResponsiveContainer))
(def line-chart (r/adapt-react-class LineChart))
(def line (r/adapt-react-class Line))
(def x-axis (r/adapt-react-class XAxis))
(def y-axis (r/adapt-react-class YAxis))
(def tooltip (r/adapt-react-class Tooltip))
(def legend (r/adapt-react-class Legend))
(def cartesian-grid (r/adapt-react-class CartesianGrid))

(def data [{:name "Page A"
            :uv   4000
            :pv   2400
            :amt  2400,}
           {:name "Page B"
            :uv   3000
            :pv   1398
            :amt  2210,}
           {:name "Page C"
            :uv   2000
            :pv   9800
            :amt  2290,}
           {:name "Page D"
            :uv   2780
            :pv   3908
            :amt  2000}
           {:name "Page E"
            :uv   1890
            :pv   4800
            :amt  2181}
           {:name "Page F"
            :uv   2390
            :pv   3800
            :amt  2500}
           {:name "Page G"
            :uv   3490
            :pv   4300
            :amt  2100}])
;


(defn example-chart []
  [chart-responsive-container {:width "100%" :height "100%"}
   [line-chart {:width  500 :height 300
                :data   data
                :margin {:top 5 :right 30 :left 20 :bottom 5}}
    [cartesian-grid {:strokeDasharray "3 3"}]
    [x-axis {:data-key "name"}]
    [y-axis]
    [tooltip]
    [legend]
    [line {:type "monotone" :dataKey "pv" :stroke "#8884d8" :activeDot {:r 8}}]
    [line {:type "monotone" :dataKey "uv" :stroke "#82ca9d"}]]])



