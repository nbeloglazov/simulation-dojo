(ns quil-simulation.core
  (:require [quil.core :refer :all]))


(def w 500)
(def h 500)
(def no-of-points 100)
(def step-size 5)

(defn create-point []
  {
   :x (rand-int w)
   :y (rand-int h)
   :id-1 (rand-int no-of-points)
   :id-2 (rand-int no-of-points)
   }
  )

(create-point)

(defn create-points []
  (repeatedly no-of-points create-point)
  )


(def points (atom (create-points)))


(defn compute-midpoint [point]
   (let [p1 (nth @points (:id-1 point))
         p2 (nth @points (:id-2 point))
         x1 (:x p1) y1 (:y p1)
         x2 (:x p2) y2 (:y p2)]
     [(/ (+ x1 x2) 2.0), (/ (+ y1 y2) 2.0)]))

(defn step-vector [point]
  (let [[xmp ymp] (compute-midpoint point)
        xp (:x point) yp (:y point)
        xv (- xmp xp) yv (- ymp yp)
        dst (sqrt (+ (* xv xv) (* yv yv)))
        step (min dst step-size)]
    (if (< -1e-5 dst 1e-5)
      [0 0]
      [(* step (/ xv dst)) (* step (/ yv dst))])))

(defn move-point [point]
  (let [[dx dy] (step-vector point)]
    (-> point
        (update-in [:x] + dx)
        (update-in [:y] + dy))))

(defn move-points [points]
  (doall (map move-point points)))

(defn draw []
  (swap! points move-points)
  (background 255)
  (fill 255 0 0)
  (doseq [point @points]
    (ellipse (:x point) (:y point) 5 5)))

(defn setup []
  (frame-rate 20))

(defsketch simulation
  :size [w h]
  :draw draw
  :setup setup)
