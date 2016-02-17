;======================================
; general settings
;======================================

(def settings
  {
    :search-type :breadth-first
    ;; :search-type :strips
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
  '{ push-up
     { :pre ( (cleartop ?x)
              (isa ?x ?_)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :del ( (cleartop ?x)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :add (
              (cleartop ?y)
              )
       :txt (push-up ?x off ?y at ?s)
       :cmd (push-from ?s)
       }
     push-down
     { :pre ( (cleartop ?y)
              (at ?y ?s)
              )
       :del ( (cleartop ?y)
              )
       :add ( (cleartop ?x)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :txt (push-down ?x on ?y at ?s)
       :cmd (push-down ?s)
       }
     push-across
     { :pre ( (cleartop ?y)
              (at ?y ?s)
              )
       :del ( (cleartop ?y)
              )
       :add ( (cleartop ?x)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :txt (push-across ?x on ?y at ?s)
       :cmd (push-across ?s)
       }
     push-to
     { :pre ( (cleartop ?y)
              (at ?y ?s)
              )
       :del ( (cleartop ?y)
              )
       :add ( (hand empty)
              (cleartop ?x)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :txt (push-to ?x on ?y at ?s)
       :cmd (push-to ?s)
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
       :post ((cleartop ?x) (on ?x ?y))
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

     :cleartop
     { :name cleartop
       :achieves (cleartop ?x)
       :when ((on ?z ?x) (at ?z ?s))
       :post ((protected ?s [cleartop ?x]) (cleartop ?z) (hand empty) )
       :pre  ((stack ?new)  (:not (protected ?new ?_))
               (at ?y ?new)  (cleartop ?y) )
       :del ((protected ?s [cleartop ?x])
              (on ?z ?x) (at ?z ?s) (cleartop ?y) )
       :add ((cleartop ?x) (at ?z ?new) (on ?z ?y)    )
       :cmd ((push-from ?s) (push-to ?new) )
       :txt ((ct-push ?z off ?x at ?s)
              (ct-push ?z on ?y at ?new) )
       }

     :push-up
     { :name push-up
       :achieves (hand empty)
       :when ((holds ?x))
       :pre  ((stack ?s) (:not (protected ?s ?_))
               (at ?y ?s) (cleartop ?y)  )
       :del  ((holds ?x) (cleartop ?y) )
       :add  ((at ?x ?s) (on ?x ?y) (hand empty) (cleartop ?x))
       :cmd  ((push-to ?s) )
       :txt  ((push ?x on ?y at ?s) )
       }

     :push-down
     { :name push-down
       :achieves (holds ?x)
       :when ((isa ?x ?_) (at ?x ?s) (on ?x ?y))
       :post ((cleartop ?x) (hand empty))
       :del  ((at ?x ?s) (on ?x ?y) (hand empty) (cleartop ?x))
       :add  ((holds ?x) (cleartop ?y) )
       :cmd  ((push-from ?s))
       :txt  ((push ?x off ?y at ?s))
       }

     :push-to
     { :name push-to
       :achieves (at ?x ?s)
       :post ((holds ?x))
       :pre  ((at ?y ?s)
               (cleartop ?y)  )
       :del  ((holds ?x) (cleartop ?y) )
       :add  ((at ?x ?s) (on ?x ?y) (hand empty) (cleartop ?x))
       :cmd  ((push-to ?s) )
       :txt  ((push ?x on ?y at ?s) )
       }

     :push-across
     { :name push-across
       :achieves (on ?x ?y)
       :when ((isa ?x ?_) (at ?y ?s))
       :post ((protected ?s [on ?x ?y])(cleartop ?y)(holds ?x))
       :pre  ()
       :del  ((holds ?x) (cleartop ?y) (protected ?s [on ?x ?y]))
       :add  ((at ?x ?s) (on ?x ?y) (hand empty) (cleartop ?x))
       :cmd  ((push-to ?s) )
       :txt  ((push ?x on ?y at ?s) )
       }

     :drop-on-held
     { :name drop-on-held
       :achieves (on ?x ?y)
       :when ((holds ?y))
       :post ((hand empty) (on ?x ?y))
       }

     :protect-x
     { :name protect-x
       :achieves (protected ?x ?c)
       :add  ((protected ?x ?c)  )
       }
     })

;====================================
; operators for executive calls
;====================================


(def exec-ops
  '{ push-up
     { :pre ( (hand empty)
              (at ?x ?s)
              (on ?x ?y)
              (cleartop ?x)
              (isa ?x ?_)
              )
       :del ( (hand empty)
              (at ?x ?s)
              (on ?x ?y)
              )
       :add ( (holds ?x)
              (cleartop ?y)
              )
       :txt (grasp! ?x off ?y at ?s)
       :cmd (pick-from ?s)
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
     push-to
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

