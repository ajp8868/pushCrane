;======================================
; general settings
;======================================

(def settings
  {
    ;;:search-type :breadth-first
    :search-type :strips
    })


;======================================
; morphology rules
;======================================

; std rules

(def sentence-morph-rules

  '( ((??a and then ??b)  => (??a stop ??b))
     ((??a and ??b)       => (??a stop ??b))
     ((??a then ??b)      => (??a stop ??b))
     ((??a now ??b)       => (??a stop ??b))
     ))

(def word-match-rules

  '( ((-> ?s is-size?)  size    => ?s)
     ((-> ?s is-size?)  sized   => ?s)
     ((-> ?c is-color?) color   => ?c)
     ((-> ?c is-color?) colour  => ?c)
     ((-> ?c is-color?) colored   => ?c)
     ((-> ?c is-color?) coloured  => ?c)

     (onto              => on-top)
     (on to             => attach-top)
     (on to the top of  => attach-top)

     (the left of       => attach-left)
     (on the left       => attach-left)
     (on to the left of => attach-left)

     (the right of       => attach-right)
     (on the right       => attach-right)
     (on to the right of => attach-right)

     (the bottom of       => attach-bottom)
     (on the bottom       => attach-bottom)
     (on to the bottom of => attach-bottom)

     (pushto         => push-to)
     (push to        => push-to)

     (push up        => push-up)
     (pushup         => push-up)

     (push down      => push-down)
     (pushdown       => push-down)

     (push across    => push-across)
     (pushacross     => push-across)

     (make a new   => finish)
     (finish       => finish)
     (finish shape => finish)
     ))


;====================================
; standard search planning operators
;====================================


(def block-ops
  '{ push
     { :pre ( (isa ?x ?_)
              (at  ?p ?c)
              )
       :del ( (at ?x bottom)
              )
       :add ( (at ?x ?y)
              )
       :txt (push ?x to middle)
       :cmd (push ?x)
       }
     })

;====================================
; goal focussed operators
;====================================


(def goal-ops
  '{

     :move-top-to
     {
       :name move-top-to
       :achieves (at tp ?x)
       :when ((:not (at tp ?x)))
       :post ((at tp ?x))
       :pre  ((:not (at tp ?x)))
       :del  ((at tp ?y))
       :add  ((at tp ?x))
       :cmd  ((move-arm "T" ?x))
       :txt  ((move top pusher to ?x))
       }

     :move-side-to
     {
       :name move-side-to
       :achieves (at sp ?x)
       :when ((:not (at sp ?x)))
       :post ((at sp ?x))
       :pre  ((:not (at sp ?x)))
       :del  ((at sp ?y))
       :add  ((at sp ?x))
       :cmd  ((move-arm "L" ?x))
       :txt  ((move side pusher to ?x))
       }

     :move-bottom-to
     {
       :name move-bottom-to
       :achieves (at bp ?x)
       :when ((:not (at bp ?x)))
       :post ((at bp ?x))
       :pre  ((:not (at bp ?x)))
       :del  ((at bp ?y))
       :add  ((at bp ?x))
       :cmd  ((move-arm "B" ?x))
       :txt  ((move bottom pusher to ?x))
       }

     :push-up
     { :name push-up
       :achieves (at ?x middle)
       :when ((isa ?x ?_) (at bp ?c) (at ?x bottom))
       :pre  ((at ?tp ?c) (clearspace middle)  )
       :del  ((at ?x ?c) (at ?x bottom))
       :add  ((at ?x middle) )
       :cmd  ((push-up bp))
       :txt  ((push-up ?x to middle))
       }

     :push-down
     { :name push-down
       :achieves (at ?x middle)
       :when ((isa ?x ?_) (at tp ?c) (at ?x top))
       :pre  ((at ?tp ?c) (clearspace middle)  )
       :del  ((at ?x ?c) (at ?x top))
       :add  ((at ?x middle) )
       :cmd  ((push-down tp))
       :txt  ((push-down ?x to middle))
       }

     :push-across
     { :name push-across
       :achieves (at ?x middle)
       :when ((isa ?x ?_) (at tp ?c) (at ?x left))
       :pre  ((at ?tp ?c) (clearspace middle)  )
       :del  ((at ?x ?c) (at ?x left))
       :add  ((at ?x middle) )
       :cmd  ((push-across lp))
       :txt  ((push-across ?x to middle))
       }

     :protect-x
     { :name protect-x
       :achieves (protected ?x ?c)
       :add  ((protected ?x ?c) )
       }
     })

;====================================
; operators for executive calls
;====================================


(def exec-ops
  '{ move-arm-x
     { :name move-arm-x
       :achieves ((at ?p ?c))
       :when ( (arm ?p)
               (retracted ?p)
               (column ?c)
               (at ?p ?oc)
               (direction ?p x)
               (nlogo-name ?p ?name)
               )
       :pre ( (arm ?p)
              (retracted ?p)
              (column ?c)
              (at ?p ?oc)
              (direction ?p x)
              (nlogo-name ?p ?name)
              )
       :del ( (at ?p ?oc)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving horizontal pusher ?p to ?c)
       :cmd ((move-pusher ?name ?c) )
       }
     move-arm-y
     { :name move-arm-y
       :achieves ((at ?p ?c))
       :when ( (arm ?p)
               (retracted ?p)
               (column ?c)
               (at ?p ?oc)
               (direction ?p y)
               (nlogo-name ?p ?name)
               )
       :pre ( (arm ?p)
              (retracted ?p)
              (column ?c)
              (at ?p ?oc)
              (direction ?p y)
              (nlogo-name ?p ?name)
              )
       :del ( (at ?p ?oc)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving vertical pusher ?p to ?c)
       :cmd ((move-pusher ?name ?c))
       }
     push-up
     { :name push-up
       :achieves ((y ?s ?y))
       :when ( (column ?c)
               (column ?y)
               (shape ?s)
               (canpush ?p bottomshapes)
               (y ?s ?oy)
               (x ?s ?c)
               (at ?p ?c)
               (nlogo-name ?p ?name))

       :pre ( (column ?c)
              (column ?y)
              (shape ?s)
              (canpush ?p bottomshapes)
              (y ?s ?oy)
              (x ?s ?c)
              (at ?p ?c)
              (nlogo-name ?p ?name)
              )
       :del ( (y ?s ?oy)
              )
       :add ( (y ?s ?y)
              )
       :txt (pushing up at column ?c from ?oy)
       :cmd ((push-shape ?name ?c ?y))
       }
      push-right
       { :name push-right
         :achieves ((x ?s ?x))
         :when ((column ?c)
                 (column ?x)
                 (shape ?s)
                 (canpush ?p sideshapes)
                 (x ?s ?ox)
                 (y ?s ?c)
                 (at ?p ?c)
                 (nlogo-name ?p ?name))
         :pre ( (column ?c)
                (column ?x)
                (shape ?s)
                (canpush ?p sideshapes)
                (x ?s ?ox)
                (y ?s ?c)
                (at ?p ?c)
                (nlogo-name ?p ?name)
                )
         :del ( (x ?s ?ox)
                )
         :add ( (x ?s ?x)
                )
         :txt (pushing right at column ?c from ?ox)
         :cmd ((push-shape ?name ?c ?x))
         }

    })

;====================================
; Planner Operators
;====================================

(def planner-ops
  '{ :move-arm-x
     { :name move-arm-x
       :achieves ((at ?p ?c))
       :when ( (arm ?p)
               (retracted ?p)
               (column ?c)
               (at ?p ?oc)
               (direction ?p x)
               (nlogo-name ?p ?name)
               )
       :pre ( (arm ?p)
              (retracted ?p)
              (column ?c)
              (at ?p ?oc)
              (direction ?p x)
              (nlogo-name ?p ?name)
              )
       :del ( (at ?p ?oc)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving horizontal pusher ?p to ?c)
       :cmd ((move-pusher ?name ?c) )
       }
     :move-arm-y
     { :name move-arm-y
       :achieves ((at ?p ?c))
       :when ( (arm ?p)
               (retracted ?p)
               (column ?c)
               (at ?p ?oc)
               (direction ?p y)
               (nlogo-name ?p ?name)
               )
       :pre ( (arm ?p)
              (retracted ?p)
              (column ?c)
              (at ?p ?oc)
              (direction ?p y)
              (nlogo-name ?p ?name)
              )
       :del ( (at ?p ?oc)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving vertical pusher ?p to ?c)
       :cmd ((move-pusher ?name ?c))
       }
     :push-up
     { :name push-up
       :achieves ((y ?s ?y))
       :when ( (column ?c)
               (column ?y)
               (shape ?s)
               (canpush ?p bottomshapes)
               (y ?s ?oy)
               (x ?s ?c)
               (at ?p ?c)
               (nlogo-name ?p ?name))

       :pre ( (column ?c)
              (column ?y)
              (shape ?s)
              (canpush ?p bottomshapes)
              (y ?s ?oy)
              (x ?s ?c)
              (at ?p ?c)
              (nlogo-name ?p ?name)
              )
       :del ( (y ?s ?oy)
              )
       :add ( (y ?s ?y)
              )
       :txt (pushing up at column ?c from ?oy)
       :cmd ((push-shape ?name ?c ?y))
       }
     :push-right
     { :name push-right
       :achieves ((x ?s ?x))
       :when ((column ?c)
               (column ?x)
               (shape ?s)
               (canpush ?p sideshapes)
               (x ?s ?ox)
               (y ?s ?c)
               (at ?p ?c)
               (nlogo-name ?p ?name))
       :pre ( (column ?c)
              (column ?x)
              (shape ?s)
              (canpush ?p sideshapes)
              (x ?s ?ox)
              (y ?s ?c)
              (at ?p ?c)
              (nlogo-name ?p ?name)
              )
       :del ( (x ?s ?ox)
              )
       :add ( (x ?s ?x)
              )
       :txt (pushing right at column ?c from ?ox)
       :cmd ((push-shape ?name ?c ?x))
       }

     })



;=============================
;World States
;=============================
(def world
  '#{(column c1)(column c2)(column c3)(column c4)(column c5)(column c6)(column c7)(column c8)(column c9)
     (at c1 c1)(at c2 c2)(at c3 c3)(at c4 c4)(at c5 c5)(at c6 c6)(at c7 c7)(at c8 c8)(at c9 c9)
     (row r1)(row r2)(row r3)(row r4)(row r5)(row r6)(row r7)(row r8)(row r9)
     (canpush arm194 sideshapes)
     (canpush arm193 bottomshapes)
     (canpush arm192 topshapes)
     (arm arm192)(arm arm193)(arm arm194)
     (direction arm192 x)
     (direction arm193 x)
     (direction arm194 y)
     (nlogo-name arm192 T)
     (nlogo-name arm193 B)
     (nlogo-name arm194 L)
     })

(def startState
  '#{(at arm192 c1)
     (at arm193 c1)
     (at arm194 c1)
     (retracted arm192)
     (retracted arm193)
     (retracted arm194)
     (shape t1)
     (shape t2)
     (shape t3)
     (x t1 c2)
     (x t2 c3)
     (x t3 c4)
     (y t1 c1)
     (y t2 c1)
     (y t3 c1)
     })

(def goalState
  '#{
     ;(at arm192 c5)
     ;(at arm193 c8)
     ;(at arm194 c4)
     (y t1 c4)
     (x t1 c7)
     ;(shapeloc t3 middle)
     })


(def plannerStartState
  '#{((column c1) (column c2) (column c3) (column c4) (column c5) (column c6) (column c7) (column c8) (column c9)
       (at c1 c1) (at c2 c2) (at c3 c3) (at c4 c4) (at c5 c5) (at c6 c6) (at c7 c7) (at c8 c8) (at c9 c9)
       (row r1) (row r2) (row r3) (row r4) (row r5) (row r6) (row r7) (row r8) (row r9)
       (canpush arm194 sideshapes)
       (canpush arm193 bottomshapes)
       (canpush arm192 topshapes)
       (arm arm192) (arm arm193) (arm arm194)
       (direction arm192 x)
       (direction arm193 x)
       (direction arm194 y)
       (nlogo-name arm192 T)
       (nlogo-name arm193 B)
       (nlogo-name arm194 L)
       (at arm192 c1)
       (at arm193 c1)
       (at arm194 c1)
       (retracted arm192)
       (retracted arm193)
       (retracted arm194)
       (shape t1)
       (shape t2)
       (shape t3)
       (x t1 c2)
       (x t2 c3)
       (x t3 c4)
       (y t1 c1)
       (y t2 c1)
       (y t3 c1)
       )}
  )



;====================================================
; mapping for resolved parses to search goals, etc
;====================================================

(declare
  goal apply-exec
  extract-to-map clear-block-data
  it-reference)

(def default-block-spec '{color grey, size med, shape cube})


(defmatch process-cmd []
  ((grasp ?x)        :=> (set-atom! it-reference (? x))   (goal (mout '(holds ?x))))

  ((move-pusher-to ?p ?c) :=> (apply-op 'at (mout '(?p sp ?c)) world))
  ((push-shape-to ?op ?c ?sn) :=> (apply-op (mout '(?op)) (mout '(?sn sp ?c)) world))

  ;((move-pusher-to ?p ?c) :=> (apply-exec ('move-arm exec-ops) (mout '{?p ?c})))



  ((create ?x ?spec) :=>
    (goal '(hand empty))
    (set-atom! it-reference (? x))
    (apply-exec ('create exec-ops)
      (conj {'x (? x)}
        (merge default-block-spec
          (extract-to-map (? spec))))))

  ((destroy ?x)      :=>
    (goal (mout '(holds ?x)))
    (apply-exec ('dispose exec-ops))
    (set-atom! it-reference false))

  ((reset)         :=> (clear-block-data) (nlogo-send "setup"))
  )



;================================
; Netlogo comms & filters
;================================


(let [sizes '{small 5, med 7, large 9}
      sp    " "
      qt    "\""
      str-qt   (fn[x] (str " \"" x "\" "))    ; wrap x in quotes
      column-no (fn[x] (apply str (rest (str x))))   ; strip "s" of stack name
      ]


  (defmatch nlogo-translate-cmd []
    ((make ?nam ?obj ?size ?color)
      :=> (str 'exec.make (str-qt (? nam)) (str-qt (? obj))
            ((? size) sizes) (str-qt (? color))))

    ;((move-to ?s)   :=> (str 'exec.move-to sp (stack-no (? s))))

    ;((drop-at ?s)   :=> (str 'exec.drop-at sp (stack-no (? s))))

    ;((push ?s) :=> (str 'exec.pick-from sp (stack-no (? s))))

    ((move-pusher ?p ?c) :=> (str 'exec.move-to (str-qt (? p)) (column-no(? c))))
    ((push-shape ?p ?c ?s) :=> (str 'pushshape (str-qt (? p)) (column-no(? c)) sp (column-no(? s))))

    ( ?_            :=> (ui-out :dbg 'ERROR '(unknown NetLogo cmd)))
    ))


;=======================================
; Test Commands
;=======================================

(defn command-caller [cmds]
  (if (not (empty? cmds))
    (do
      (print cmds)
      (nlogo-send-exec (first (first cmds)))
      (command-caller (rest cmds)))
    (println "finished")
    )
  )

(defn test1 []
  (nlogo-send "startup")
  (nlogo-send "setUpShapes")
    (command-caller (:cmds (ops-search startState goalState exec-ops :world world)
                           ))
  )

(defn test2 []
  (nlogo-send "startup")
  (nlogo-send "setUpShapes")
  (command-caller (:cmds (planner plannerStartState '(y t3 5) planner-ops)
                         ))
  )

