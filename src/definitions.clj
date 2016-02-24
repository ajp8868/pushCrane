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
  '{ push-up
     { :pre ((at ?p ?c)
              (isa ?p horse)
              )
       :txt (pushing up at column ?c)
       :cmd (push-up ?s)
       }
     move-arm-x
     { :pre ( (arm ?p)
              (retracted ?p)
              (column ?c)
              (at ?p ?c2)
              (direction ?p x)
              (nlogo-name ?p ?name)
              )
       :del ( (at ?p ?c2)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving horizontal pusher ?p to ?c)
       :cmd ((move-pusher ?name ?c) )
       }
     move-arm-y
     { :pre ( (arm ?p)
              (retracted ?p)
              (column ?c)
              (at ?p ?c2)
              (direction ?p y)
              (nlogo-name ?p ?name)
              )
       :del ( (at ?p ?c2)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving vertical pusher ?p to ?c)
       :cmd ((move-pusher ?name ?c))
       }
     push-down
     { :pre ( (holds ?x)
              (at ?dst ?s)
              (cleartop ?dst)
              )
       :del ( (holds ?x)
              (cleartop ?dst)
              )
       :add ( (hand empty)
              (on ?x ?dst)
              (at ?x ?s)
              (cleartop ?x)
              )
       :txt (put! ?x on ?dst at ?s)
       :cmd (drop-at ?s)
       }
     push-across
     { :pre ( (holds ?x)
              (at ?dst ?s)
              (cleartop ?dst)
              )
       :del ( (holds ?x)
              (cleartop ?dst)
              )
       :add ( (hand empty)
              (on ?x ?dst)
              (at ?x ?s)
              (cleartop ?x)
              )
       :txt (put! ?x on ?dst at ?s)
       :cmd (drop-at ?s)
       }
     create
     { :pre ( (hand empty) )
       :del ( (hand empty) )
       :add ( (isa  ?x ?isa)
              (size ?x ?size)
              (color ?x ?color)
              (holds ?x)
              )
       :txt (make! ?x ?isa size= ?size color= ?color)
       :cmd (make ?x ?isa ?size ?color)
       }
     dispose
     { :pre ( (holds ?x)        (isa ?x ?obj)
              (color ?x ?color) (size ?x ?size)
              )
       :del ( (holds ?x) (cleartop ?x) (isa ?x ?obj)
              (color ?x ?color) (size ?x ?size)
              )
       :add ( (hand empty) )
       :txt (dispose! ?x)
       :cmd (dispose)
       }})








;=============================
;World States
;=============================
(def world
  '#{(column c1)(column c2)(column c3)(column c4)(column c5)(column c6)(column c7)
     (at c1 c1)(at c2 c2)(at c3 c3)(at c4 c4)(at c5 c5)(at c6 c6)(at c7 c7)
     (connects c1 c2)(connects c2 c3)(connects c3 c4)(connects c4 c5)(connects c5 c6)(connects c6 c7)
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
     })

(def goalState
  '#{(at arm192 c5)
     (at arm194 c6)
     (at arm193 c7)
     })




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

    ( ?_            :=> (ui-out :dbg 'ERROR '(unknown NetLogo cmd)))
    ))

(defn command-caller [cmds]
  (if (not (empty? cmds))
    (do
      (nlogo-send-exec (first (first cmds)))
      (command-caller (rest cmds)))
    )
  )

(defn test1 []
    (command-caller (:cmds (ops-search startState goalState exec-ops :world world)
                           ))
  )

