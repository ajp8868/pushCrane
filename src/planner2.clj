(def ops
  '{moveVert {:pre ( (isa ?block block)
                     (isa ?col coly)
                     (isa ?col3 colx)
                     (isa ?col2 coly)
                     (at ?block ?col)
                     (at ?block ?col3)
                     )
              :add ( (at ?block ?col2))
              :del ( (at ?block ?col))
              :txt ( (move ?block from ?col to ?col2))
              :cmd ( moveVert ?col3 ?col2 ?col)
              }
    moveHoroz {:pre ( (isa ?block block)
                                 (isa ?col colx)
                                 (isa ?col3 coly)
                                 (isa ?col2 colx)
                                 (at ?block ?col)
                                 (at ?block ?col3)
                                 )
                          :add ( (at ?block ?col2))
                          :del ( (at ?block ?col))
                          :txt ( (move ?block from ?col to ?col2))
                          :cmd (moveHoz L ?col3 ?col2 ?col)
                          }
      })



(def planner-world
  (union state world)
  )
;((at ?block ?col)
;  (at ?block ?col3))
  (def ops2
    '{:moveVert{:name moveVert
                :achieves (at ?block1 ?col2)
                :post ()
                :when ( (isa ?block1 ?block)
                        (isa ?col coly)
                        (isa ?col3 colx)
                        (isa ?col2 coly)
                        )
                :del (  (at ?block1 ?col))
                :add ( (at ?block1 ?col2))
                :pre ()
                :txt  ((move ?block1 from ?col to ?col2 ))
                :cmd (moveVert ?col3 ?col2 ?col)
                }
      :moveHoroz{:name moveHoroz
                 :achieves (at ?block ?col2)
                 :post ()
                 :when ( (isa ?block block)
                         (isa ?col colx)
                         (isa ?col3 coly)
                         (isa ?col2 colx)
                         )
                 :del ( (at ?block ?col))
                 :add ( (at ?block ?col2))
                 :pre ()
                 :txt ( (move ?block from ?col to ?col2))
                 :cmd (moveHoz L ?col3 ?col2 ?col)
                 }
      :moveArmHorz{:name moveArmHoroz
                   :achives (at ?arm ?col2)
                   :post ()
                   :when ( (isa ?arm arm)
                           (at ?arm ?col)
                           (isa ?col2 colx)
                           )
                   :del ( (at ?arm ?col))
                   :add ( (at ?arm ?col2))
                   :pre()
                   :txt ((move ?arm from ?col to ?col2))
                   :cmd (moveArmHoroz ?arm ?col2)
                   }

      }
    )


