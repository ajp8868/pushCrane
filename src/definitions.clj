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
     { :pre ( (hand empty)
              (cleartop ?x)
              (isa ?x ?_)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :del ( (hand empty)
              (cleartop ?x)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :add ( (holds ?x)
              (cleartop ?y)
              )
       :txt (pick ?x off ?y at ?s)
       :cmd (pick-from ?s)
       }
     drop
     { :pre ( (holds ?x)
              (cleartop ?y)
              (at ?y ?s)
              )
       :del ( (holds ?x)
              (cleartop ?y)
              )
       :add ( (hand empty)
              (cleartop ?x)
              (on  ?x ?y)
              (at  ?x ?s)
              )
       :txt (drop ?x on ?y at ?s)
       :cmd (drop-at ?s)
       }
     })
