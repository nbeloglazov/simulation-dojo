(ns quil-simulation.core
  (:require [quil.core :refer :all]))


(def w 500)
(def h 500)
(def no-of-points 100)
(def step-size 5)

(defn new-veloc []
  (* (- (rand 2) 1) (/ step-size 2.)))

(defn update-veloc [point]
  (if (< (rand) 0.1)
    (assoc point
      :vx (new-veloc)
      :vy (new-veloc))
    point))

(defn create-point []
  {
   :x (rand-int w)
   :y (rand-int h)
   :id-1 (rand-int no-of-points)
   :id-2 (rand-int no-of-points)

   :vx (new-veloc)
   :vy (new-veloc)
   :step (- (rand (* 2 step-size)) 2)
   }
  )


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
        step (min dst (:step point))]
    (if (< dst 30)
      [0 0]
      [(* step (/ xv dst)) (* step (/ yv dst))])))

(defn wrap [val min max]
  (cond
   (< val min) (+ max val)
   (> val max) (- val max)
   :else val
   )
  )

(defn boundary [point]
    (-> point
        (update-in [:x] wrap 0 w )
        (update-in [:y] wrap 0 h )))


(defn move-point [point]
  (let [[dx dy] (step-vector point)]
    (-> point
        (update-in [:x] + dx (:vx point))
        (update-in [:y] + dy (:vy point))
        (boundary)
        (update-veloc)
        )))

(defn move-points [points]
  (doall (map move-point points)))

(defn draw-vector [point]
  (let [[xmp ymp] (compute-midpoint point)]
    (line (:x point) (:y point) xmp ymp)
    )
  )

(defn draw []
  (swap! points move-points)
  (background 255)
  (fill 255 0 0)
  (doseq [point @points]
    (ellipse (:x point) (:y point) 5 5)
    ;(draw-vector point)
    ))

(defn setup []
  (frame-rate 20))

(defsketch simulation
  :size [w h]
  :draw draw
  :setup setup)

