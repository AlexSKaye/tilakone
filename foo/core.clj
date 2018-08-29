(ns tilakone.core
  (:require [tilakone.util :as u]))

(def _
  "Special symbol to denote that the transition matches any signal"
  ::_)

(defn apply-signal [fsm signal]
  (let [current-state-name (-> fsm :state)
        current-state      (-> fsm (u/get-state current-state-name))
        transition         (-> fsm (u/get-transition current-state signal))
        next-state-key     (-> transition :to)
        next-state         (-> fsm (u/get-state next-state-key))]
    (if (= next-state-key current-state-name)
      (-> fsm
          (u/apply-fsm-actions signal (-> transition :actions))
          (u/apply-fsm-actions signal (-> current-state :stay)))
      (-> fsm
          (u/apply-fsm-actions signal (-> current-state :leave))
          (assoc :state next-state-key)
          (u/apply-fsm-actions signal (-> transition :actions))
          (u/apply-fsm-actions signal (-> next-state :enter))))))


(comment

  ;
  ; The FSM looks like this:
  ;

  (def FSM
    {:states  [{:name        Any   ; State name (can be string, keyword, symbol, any clojure value)
                :desc        Str   ; State description
                :transitions [{:name    Any        ; Transition name
                               :desc    Str        ; Transition description
                               :to      Any        ; Name of the next state
                               :on      Matcher    ; Data for match?, does the signal match this transition?
                               :guards  [Guard]    ; Data for guard?, is this transition allowed?
                               :actions [Action]}] ; Actions to be performed on this transition
                :enter       [Action]     ; Actions to be performed when entering this state
                :leave       [Action]     ; Actions to be performed when leaving this state
                :stay        [Action]}]   ; Actions to be performed when signal is processed, but state remains the same
     :match?  (fn [value signal matcher] ... true/false)  ; Signal matching predicate
     :guard?  (fn [value signal guard] ... true/false)    ; Guard matching predicate
     :action! (fn [value signal action] ... value)        ; Action function
     :state   Any     ; Current state
     :value   Any})   ; Current value

  )
