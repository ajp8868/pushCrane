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
  '{ :move-x1   ;; a handy multi-move operator when x & y on same s
     ;     { :name move-x1
     ;       :achieves (on ?x ?y)
     ;       :when ((isa ?x ?_) (at ?x ?s) (at ?y ?s))
     ;       :post ((protected ?s [on ?x ?y]) (holds ?x) (on ?x ?y))
     ;       :del ((protected ?s [on ?x ?y]) )
     ;       }
     { :name move-x1
       :achieves (on ?x ?y)
       :when ((isa ?x ?_) (at ?x ?s) (at ?y ?s))
       :post ((clearspace ?x) (on ?x ?y))
       }

     :move-x   ;; a handy multi-move operator
     { :name move-x
       :achieves (on ?x ?y)
       :when ((isa ?x ?_) (at ?x ?sx) (at ?y ?sy) )
       :post ((protected ?sx [on ?x ?y]) (protected ?sy [on ?x ?y])
               (cleartop ?x) (cleartop ?y) )
       :pre ((on ?x ?ox) )
       :del ((at ?x ?sx)  (on ?x ?ox) (cleartop ?y)
              (protected ?sx [on ?x ?y]) (protected ?sy [on ?x ?y]) )
       :add ((at ?x ?sy) (on ?x ?y) (cleartop ?ox))
       :cmd ((push-from ?sx) (push-to ?sy) )
       :txt ((mv-push ?x off ?ox at ?sx)
              (mv-push ?x on ?y at ?sy) )
       }

     :move-top-to
     {
       :name move-top-to
       :achieves (at tp ?x)
       :when ((:not (at tp ?x)))
       :post ((at tp ?x))
       :pre  ((:not (at tp ?x)))
       :del  ((at tp ?y))
       :add  ((at tp ?x))
       :cmd  ((exec.move-to "T" ?y))
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
       :cmd  ((exec.move-to "L" ?y))
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
       :cmd  ((exec.move-to "B" ?y))
       :txt  ((move bottom pusher to ?x))
       }

     :clearspace
     { :name clearspace
       :achieves (clearspace ?x)
       :when ((on ?z ?x) (at ?z ?c))
       :post ( (clearspace ?z) )
       :pre  ((column ?new)
               (at ?y ?new)  (clearspace ?y) )
       :del ( (at ?x ?c) (at ?x ?y) )
       :add ((clearspace ?x) (at ?z ?new) (on ?z ?y)    )
       :cmd ((push-from ?c) (push-to ?new) )
       :txt ((ct-push ?z off ?x at ?c)
              (ct-push ?z on ?y at ?new) )
       }

     :push-up
     { :name push-up
       :achieves (at ?x middle)
       :when ((isa ?x ?_) (at ?x bottom) (at ?x ?c))
       :pre  ((at ?bp ?c) (clearspace middle)  )
       :del  ((at ?x bottom) (clearspace ?y) )
       :add  ((at ?x ?c) (clearspace ?x))
       :cmd  ((push-up ?c) )
       :txt  ((push-up ?x to middle) )
       }

     :push-down
     { :name push-down
       :achieves (at ?x middle)
       :when ((isa ?x ?_) (at ?x ?c) (at ?x top))
       :post ((clearspace ?x))
       :pre  ((at ?tp ?c) (clearspace middle)  )
       :del  ((at ?x ?c) (at ?x top) (clearspace ?y))
       :add  ((clearspace ?y) )
       :cmd  ((push-down ?c))
       :txt  ((push-down ?x to middle))
       }

     :push-across
     { :name push-across
       :achieves (at ?x middle)
       :when ((isa ?x ?_) (at ?x ?c) (at ?x left))
       :post ((clearspace ?y)(at ?x middle))
       :pre  ((at ?lp ?c) (clearspace middle)  )
       :del  ((holds ?x) (cleartop ?y) (protected ?s [on ?x ?y]))
       :add  ((at ?x ?s) (on ?x ?y) (hand empty) (cleartop ?x))
       :cmd  ((push-to ?s) )
       :txt  ((push ?x on ?y at ?s) )
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
     { :pre ( (at ?p ?c)
              )
       :txt (pushing up at column ?c)
       :cmd (pick-from ?s)
       }
     move-arm
     { :pre ( (hand retracted)
              )
       :del ( (at ?p ?c)
              )
       :add ( (at ?p ?c)
              )
       :txt (Moving ?p to ?c)
       :cmd (move-pusher ?p ?c)
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

  ((move-pusher-to ?p ?c) :=> (apply-exec ('move-arm exec-ops) (mout '{?p ?c})))



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
      stack-no (fn[x] (apply str (rest (str x))))   ; strip "s" of stack name
      ]


  (defmatch nlogo-translate-cmd []
    ((make ?nam ?obj ?size ?color)
      :=> (str 'exec.make (str-qt (? nam)) (str-qt (? obj))
            ((? size) sizes) (str-qt (? color))))
    ((move-to ?s)   :=> (str 'exec.move-to sp (stack-no (? s))))
    ((drop-at ?s)   :=> (str 'exec.drop-at sp (stack-no (? s))))
    ((push ?s) :=> (str 'exec.pick-from sp (stack-no (? s))))
    ((move-pusher ?p ?c) :=> (str 'exec.move sp str-qt (? p) sp (? c)))
    ( ?_            :=> (ui-out :dbg 'ERROR '(unknown NetLogo cmd)))
    ))

