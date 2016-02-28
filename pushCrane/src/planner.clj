(defn print-goals [q]
  (if (not (empty? q))
    (do
      (ui-out :dbg "GOALS:")
      (doseq [x q]
        (ui-out :dbg "      " (if (map? x) [(:name x) :=> (:achieves x)] x))
        )
      ;(ui-out :dbg '------)
      )
    ))


(def goalq (atom (java.util.concurrent.LinkedBlockingDeque.)))

(defn ui-out [tag msg & r]
  (println tag \tab msg r))

(declare strips-loop update-path
  goal-mop-apply apply-goal-op)
;
;(defn planner [state goal goal-ops]
;  (.clear @goalq)
;  (.push @goalq goal)
;  (strips-loop {:state state, :cmds nil, :txt nil} goal-ops 60))

(defn planner [state goal goal-ops]
  (.clear @goalq)
  (doseq [p goal] (.push @goalq p))
  (strips-loop {:state state, :cmds nil, :txt nil} goal-ops 60))

(defn strips-loop
  [path goal-ops limit]
  (if (zero? limit)
    (throw (new RuntimeException "limit exceeded in run-goal-ops")))

  ;(println path)
  (print-goals @goalq)

  (if-let [goal (.poll @goalq)]
    (cond
      (map? goal) ;; it is a partially matched op
      (do
        (ui-out :dbg '** 'APPLYING (:name goal) '=> (:achieves goal))
        ; (ui-out :dbg '** (:add goal))
        (recur
          (update-path path (goal-mop-apply (:state path) goal))
          goal-ops (dec limit))
        )

      ;; else it is a fact
      (not (contains? (:state path) goal))
      (do (ui-out :dbg 'solving goal)
        (some (partial apply-goal-op (:state path) goal)
          (vals goal-ops))
        (recur path goal-ops (dec limit))
        )
      ;; else it is an existing fact
      :else
      (recur path goal-ops (dec limit))
      )
    path
    )
  )


(defn goal-mop-apply [bd mop]
  (mfind* [(:pre mop) bd]
    (ui-out :dbg '** (mout (:add mop)))
    ; (ui-out :dbg '=> (mout mop))
    {:state (union (mout (:add mop))
              (difference bd (mout (:del mop))))
     :cmd   (mout (:cmd mop))
     :txt   (mout (:txt mop))
     }
    ))


(defn apply-goal-op [bd goal op]
  (println (list 'trying (:name op)))
  (mlet [(:achieves op) goal]

    (mfind* [(:when op) bd]
      (ui-out :dbg 'using=> (:name op))
      (let [mop (mout op)]
        ;(println (list 'new-mop mop))
        (.push @goalq mop)
        (ui-out :dbg 'new-goals (or (:post mop) '-none))
        (doseq [p (reverse (:post mop))]
          (.push @goalq p))

        ;(println (list 'succeeded (:name op)))
        true
        ))
    ))


(defn update-path
  [current newp]
  { :state (:state newp),
    :cmds  (concat (:cmds current) (:cmd newp)),
    :txt   (concat (:txt current) (:txt newp))
    })
