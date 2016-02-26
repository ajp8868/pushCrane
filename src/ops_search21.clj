(ns ops-search2)

(def ops2
  '{pushTop {:pre ( (block ?block)
                    (arm ?arm)
                    (connects ?c12 ?c13)
                    (connects ?c1 ?c2)
                    (at ?block ?c12)
                    (on ?block ?c1)
                    (at Toparm ?c1)
                 )
          :txt ( (move ?block from ?c12 to ?c13))
             :cmd [push Toparm ?c1 ?c13]
             :add ( (at ?block ?c13))
             :del ( (at ?block ?c12))
             }
    pushBot {:pre ( (block ?block)
                    (arm ?arm)
                    (connects ?c1 ?c2)
                    (at ?block ?c12)
                    (on ?block ?c1)

                    )
             }
    moveArm {:pre  ( (arm ?arm)
                     (at ?arm ?col1)
                     (connects ?col1 ?col2))
             :add  ( (at ?arm ?col2))
             :del  ( (at ?arm ?col1))
             :txt  ( (move ?arm from ?col1 to ?col2))
             :cmd  (move ?arm ?col2)
             }}
  )

 (def ops
  '{
;      moveVert {:pre ( (isa ?block block)
;                    ;  (created ?block true)
;                      (isa ?col coly)
;                      (isa ?col3 colx)
;                      (isa ?col2 coly)
;                      (at ?block ?col)
;                      (at ?block ?col3)
;                      )
;               :add ( (at ?block ?col2))
;               :del ( (at ?block ?col))
;               :txt ( (move ?block from ?col to ?col2))
;               :cmd ( moveVert ?col3 ?col2 ?col)
;               }
;     moveHoroz {:pre ( (isa ?block block)
;                      ; (created ?block true)
;                       (isa ?col colx)
;                       (isa ?col3 coly)
;                       (isa ?col2 colx)
;                       (at ?block ?col)
;                       (at ?block ?col3)
;                       )
;                :add ( (at ?block ?col2))
;                :del ( (at ?block ?col))
;                :txt ( (move ?block from ?col to ?col2))
;                :cmd (moveHoz L ?col3 ?col2 ?col)
;                }
     createBlock{:pre ( (isa ?block block)
                        (crt ?block fls)
                        (crtx ?block ?crtx1)
                        (crty ?block ?crty2)
;                        (at B ?crtx1)
;                        (at L ?crty2)
                        )
                 :add ( (crt ?block tr))                 :del ( (crt ?block fls))
                 :txt ( (crt the block ?block))
                 :cmd (create ?block )
                 }
     moveArmHoroz{:pre ( (isa ?arm arm)
                    (at ?arm ?col)
                    (isa ?col2 colx))
               :add ( (at ?arm ?col2))
               :del ( (at ?arm ?col))
               :txt ( (moving arm from ?col to ?col2))
               :cmd (moveArmHoroz ?arm ?col2)
             }
;     moveArmVert{:pre ( (isa ?arm arm)
;                         (at ?arm ?col)
;                         (isa ?col2 coly))
;                  :add ( (at ?arm ?col2))
;                  :del ( (at ?arm ?col))
;                  :txt ( (moving ?arm from ?col to ?col2))
;                  :cmd (moveArmHoroz ?arm ?col2)
;                  }
      })

;;(ops-search state2 '((at T 3)) ops2)
 (def state
   '#{
      (at T c1)
      (at B c1)
      (at L y1)

      (at blueT c2)
      (at blueT y1)
      (crt blueT fls)


      (at blueC c1)
      (at blueC y2)
      }
   )
 (def world
   '#{
      (crt tr alive)
      (crt fls alive)

      (isa T arm)
      (isa B arm)
      (isa L arm)

      (isa blueT block )
      (crtx blueT y2)
      (crty blueT c1)



      (isa greenT block )
      (isa yellowT block )
      (isa redT block )

      (isa blueC block )
      (isa greenC block )
      (isa yellowC block )
      (isa redC block )

      (isa block blueS)
      (isa block greenS)
      (isa block yellowS)
      (isa block redS)

      (isa c1 colx) (isa c2 colx) (isa c3 colx);(isa c4 colx)(isa c5 colx)(isa c6 colx)(isa c7 colx)(isa c8 colx)
      (isa y1 coly) (isa y2 coly)  (isa y3 coly);(isa y4 coly)(isa y5 coly)(isa y6 coly)(isa y7 coly)(isa y8 coly)

      })

(def state2
        '#{(arm T)
           (arm B)
           (block redblock)
           (connects 1 3)
           (at T 1)
           (at B 1)
           (at redblock 1)
           }
  )
;get the cmds from results and send it to nlogo-send-exec, which sends it to nlogo-translate-cmd that then send it to netlogo






;
;
; (def ops3
;   '{pushVert {:pre  ( (block ?block)
;                       (arm ?arm)
;                       (xcol ?colx)
;                       ; (ycol ?coly)
;                       (connects ?colx1 ?colx2)
;                       (at ?arm ?colx1)
;                       (at ?block ?colx1)
;                       ;    (at ?block ?coly)
;                       )
;               :add ( (at ?block ?coly2)
;                      )
;               :del ( (at ?block ?coly)
;                      )
;               :txt ( (move ?block from ?colx , ?coly to ?colx , ?coly2))
;               :cmd [pushshape ?arm ?colx 1]
;              }
;     }
;   )
;
;(def state3
;  '#{(arm topArm)
;     (arm bottomArm)
;     (arm leftArm)
;     (block redblock)
;     (xcol colx1)                                           ; (ycol coly1)
;     (xcol colx2)                                           ; (ycol coly2)
;     (xcol colx3)                                           ;(ycol coly3)
;     (connects colx1 colx2) (connects colx2 colx3)
;     ; (connects coly1 coly2) (connects coly2 coly3)
;     (at toparm colx2)
;     (at redblock colx2)
;     ;    (at redblock coly2)
;     }
;  )
;
;(def ops
;  '{pickup {:pre ( (agent ?agent)
;                   (manipulable ?obj)
;                   (at ?agent ?place)
;                   (on ?obj   ?place)
;                   (holds ?agent nil)
;                   )
;            :add ((holds ?agent ?obj))
;            :del ((on ?obj   ?place)
;                   (holds ?agent nil))
;            :txt (pickup ?obj from ?place)
;            :cmd [grasp ?obj]
;            }
;    drop    {:pre ( (at ?agent ?place)
;                    (holds ?agent ?obj)
;                    (:guard (? obj))
;                    )
;             :add ( (holds ?agent nil)
;                    (on ?obj   ?place))
;             :del ((holds ?agent ?obj))
;             :txt (drop ?obj at ?place)
;             :cmd [drop ?obj]
;             }
;    move    {:pre ( (agent ?agent)
;                    (at ?agent ?p1)
;                    (connects ?p1 ?p2)
;                    )
;             :add ((at ?agent ?p2))
;             :del ((at ?agent ?p1))
;             :txt (move ?p1 to ?p2)
;             :cmd [move ?p2]
;             }
;    })
;
;(def state1
;  '#{(at R table)
;     (on book table)
;     (on spud table)
;     (holds R nil)
;     (connects table bench)
;     (manipulable book)
;     (manipulable spud)
;     (agent R)
;     })

;
;(let [sizes '{small 5, med 7, large 9}
;      sp    " "
;      qt    "\""
;      str-qt   (fn[x] (str " \"" x "\" "))    ; wrap x in quotes
;      stack-no (fn[x] (apply str (rest (str x))))   ; strip "s" of stack name
;      ]
;
;
;     (defmatch nlogo-translate-cmd []
;               ((make ?nam ?obj ?size ?color)
;                 :=> (str 'exec.make (str-qt (? nam)) (str-qt (? obj))
;                          ((? size) sizes) (str-qt (? color))))
;               ((move ?a ?c)
;                 :=> (str 'exec.move-to sp (str-qt (? a) sp (? c))))
;               ((drop-at ?s)
;                 :=> (str 'exec.drop-at sp (stack-no (? s))))
;               ((pick-from ?s)
;                 :=> (str 'exec.pick-from sp (stack-no (? s))))
;               ((push ?arm ?col ?ncol)
;                 :=> (str 'pushshape (str-qt (? arm)) (str-qt (? col)) (str-qt (? ncol))))
;               ( ?_            :=> (ui-out :dbg 'ERROR '(unknown NetLogo cmd)))
;               ))