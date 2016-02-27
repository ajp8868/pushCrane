(ns pussher.definitions1)

;(ns pussher.definitions)
;================================
; Initiating comunication with netlogo
;================================
(defn startup [port]

  (set-shrdlu-comms port)

  )
;================================
; planner operators
;================================
(def planner-ops
  '{

    :moveArm-vert{:name moveArm-vert
                  :achieves (at ?arm ?col2)
                  :post ()
                  :when ( (isa ?col2 coly)
                          (at ?arm ?col)
                          (isa ?col coly)
                          (isa ?arm arm))
                  :del (  (at ?arm ?col))
                  :add ( (at ?arm ?col2))
                  :pre ()
                  :txt  ((move ?arm from ?col to ?col2 ))
                  :cmd ((moveArm ?arm ?col2))
                  }
    :moveArm-hoz{:name moveArm-hoz
                 :achieves (at ?arm ?col2)
                 :post ()
                 :when ( (isa ?col2 colx)
                         (isa ?arm arm)
                         (at ?arm ?col)
                         (isa ?col colx)
                         )
                 :del (  (at ?arm ?col))
                 :add ( (at ?arm ?col2))
                 :pre ()
                 :txt  ((move ?arm from ?col to ?col2 ))
                 :cmd ((moveArm ?arm ?col2))
                 }
    :crtBlock{:name crtBlock
              :achieves (crt ?block tr)
              :when ( (crt ?block fls)
                      (crtx ?block ?crtx1)
                      (crty ?block ?crty1)
                      )
              :post ( (at B ?crtx1)
                      (at L ?crty1))
              :pre((isa ?block block)
                    (crt ?block fls)
                    )
              :add ((crt ?block tr))
              :del ((crt ?block fls))
              :cmd ((create ?block))
              :txt ((creat ?block ))
              }
    :push-vert{:name push-vertical
               :achieves (at ?block ?col2)
               :when ( (aty ?block ?col)
                       (atx ?block ?col4)
                       (aty ?block ?col2)
                       (isa ?col coly)
                       (isa ?col4 coly)
                       )
               :post ((crt ?block tr))
               :pre (())
               :add ( (at ?block ?col2))
               :del (  (at ?block ?col))
               :cmd ((push-vert ?col4 ?col2 ?col))
               :txt  ((move ?block from ?col to ?col2 ))
               }
    :push-hoz{:name push-hoz
              :achieves (at ?block ?col2)
              :when ( (isa ?block block)
                      (at ?block ?col)
                      (isa ?col colx)
                      (isa ?col2 colx)
                      )
              :post ((crt ?block tr))
              :del (  (at ?block ?col))
              :pre ()
              :add ( (at ?block ?col2))
              :txt  ((move ?block from ?col to ?col2 ))
              :cmd ((push-hoz ?col4 ?col2 ?col))
              }
    }
  )
;================================
; ops-search operators
;================================

(def ops
  '{
    push-vert {:pre ((isa ?block block)
                      (crt ?block tr)
                      (isa ?col coly)
                      (isa ?col3 colx)
                      (isa ?col2 coly)
                      (at ?block ?col)
                      (at ?block ?col3)
                      )
               :add ((at ?block ?col2))
               :del ((at ?block ?col))
               :txt ((move ?block from ?col to ?col2))
               :cmd (push-vert ?col3 ?col2 ?col)
               }
    push-hoz {:pre ((isa ?block block)
                     (crt ?block tr)
                     (isa ?col colx)
                     (isa ?col3 coly)
                     (isa ?col2 colx)
                     (at ?block ?col)
                     (at ?block ?col3)
                     )
              :add ((at ?block ?col2))
              :del ((at ?block ?col))
              :txt ((move ?block from ?col to ?col2))
              :cmd (push-hoz L ?col3 ?col2 ?col)
              }
    createBlock{:pre ( (isa ?block block)
                       (crt ?block fls)
                       (crtx ?block ?crtx)
                       (crty ?block ?crty)
                       (at B ?crtx)
                       (at L ?crty)
                       )
                :add ( (crt ?block tr)
                       (at ?block ?crtx)
                       (at ?block ?crty)
                       )
                :del ( (crt ?block fls))
                :txt ( (crt the block ?block B at ?crtx L at ?crty))
                :cmd (create ?block )
                }
    moveArmHoroz{:pre ( (isa ?arm arm)
                        (at ?arm ?col)
                        (isa ?col colx)
                        (isa ?col2 colx))
                 :add ( (at ?arm ?col2))
                 :del ( (at ?arm ?col))
                 :txt ( (moving ?arm from ?col to ?col2))
                 :cmd (moveArm ?arm ?col2)
                 }
    moveArmVert{:pre ( (isa ?arm arm)
                       (at ?arm ?col)
                       (isa ?col coly)
                       (isa ?col2 coly))
                :add ( (at ?arm ?col2))
                :del ( (at ?arm ?col))
                :txt ( (moving ?arm from ?col to ?col2))
                :cmd (moveArm ?arm ?col2)
                }

    }
  )
;================================
; intiel state
;================================
(def state
  '#{

     (at T c1)
     (at B c1)
     (at L y1)

     (at Tblue C1)
     (at Tblue y1)

     (at Cblue c1)
     (at Cblue y1)

     (crt Tblue fls)
     (crt Tred fls)
     (crt Tyellow fls)
     (crt Tgreen fls)

     (crt Cblue fls)
     (crt Cred fls)
     (crt Cyellow fls)
     (crt Cgreen fls)

     (crt Sblue fls)
     (crt Sred fls)
     (crt Syellow fls)
     (crt Sgreen fls)
     }
  )


;================================
; World state
;================================

(def world
  '#{
     (crt tr alive)
     (crt fls alive)

     (isa T arm)
     (isa B arm)
     (isa L arm)

     (isa Tblue block )
     (crtx Tblue c2)
     (crty Tblue y1)

     (isa Tyellow block )
     (crtx Tyellow c4)
     (crty Tyellow y1)

     (isa Tgreen block )
     (crtx Tgreen c3)
     (crty Tgreen y1)

     (isa Tred block )
     (crtx Tred c4)
     (crty Tred y1)

     (isa Cblue block )
     (crtx Cblue c2)
     (crty Cblue y8)

     (isa Cgreen block )
     (crtx Cgreen c3)
     (crty Cgreen y8)

     (isa Cyellow block )
     (crtx Cyellow c4)
     (crty Cyellow y8)

     (isa Cred block )
     (crtx Cred c5)
     (crty Cred y8)

     (isa Sblue block )
     (crtx Sblue c1)
     (crty Sblue y2)

     (isa Sgreen block )
     (crtx Sgreen c1)
     (crty Sgreen y3)

     (isa Syellow block )
     (crtx Syellow c1)
     (crty Syellow y4)

     (isa Sred block )
     (crtx Sred c1)
     (crty Sred y5)

     (isa c1 colx) (isa c2 colx) (isa c3 colx)(isa c4 colx)(isa c5 colx)(isa c6 colx)(isa c7 colx)(isa c8 colx);
     (isa y1 coly) (isa y2 coly) (isa y3 coly)(isa y4 coly)(isa y5 coly)(isa y6 coly)(isa y7 coly)(isa y8 coly);

     })
;================================
; Netlogo comms & filters
;================================


(let [
      sp    " "
      qt    "\""
      str-qt   (fn[x] (str " \"" x "\" "))    ; wrap x in quotes
      axis-no (fn[x] (apply str (rest (str x))))   ; strip first letter of axis name
      ]


  (defmatch nlogo-translate-cmd []


            ((create ?nam )
              :=> (str 'exec.make (str-qt (? nam)) sp (str-qt(axis-no (? nam)))  ))
            ((push-vert ?ax ?to ?from)
              := (str 'pushshapeTwo sp (axis-no(? ax)) sp (axis-no(? to)) sp (axis-no(? from))))
            ((push-hoz ?arm ?ax ?to ?from)
              := (str 'pushshapeOne  (str-qt (? arm)) sp (axis-no(? ax)) sp (axis-no(? to)) sp (axis-no(? from))))
            ((moveArm ?a ?c)
              :=> (str 'exec.move-to sp (str-qt (? a)) sp (axis-no(? c))))
            ((repl ?d)
              :=> (str 'finrepl sp  sp (? d)))

            ))

