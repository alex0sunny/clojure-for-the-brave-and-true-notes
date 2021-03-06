(ns true-ale.core)

(def order-details
  {:name "Mitchard Blimmons"
   :email "mitchard.blimmonsexample.com"})

; What we want to see:
;(validate-order order-details order-details-validations)
; => {:email ["Your email address doesn't look like an email address."]}

(def order-details-validations
  {:name ["Please enter a name" not-empty]
   :email ["Please enter an email address" not-empty
           "Your email address doesn't look like an email address" #(or (empty? %) (re-seq #"@" %))]})

(defn error-messages-for
  "Return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

(defn validate
  "Returns a map with a vector of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(validate order-details order-details-validations)

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

(if-valid order-details
          order-details-validations
          my-error-name
          (println :success)
          (println :failure my-error-name))

;; Exercises
;;
;; 1. Write the macro when-valid so that it behaves similarly to when.
(def order-details-validation
  ["Please enter a name" not-empty])

(defmacro when-valid
  "Run body when valid"
  [to-validate validations & then]
  `(if (empty? (validate ~to-validate ~validations))
     (do
       ~@then)))

(def valid-order-details
  {:name "Foo"
   :email "matt@example.com"})

(when-valid valid-order-details
            order-details-validations
            (println "It's a success!")
            (println "render success"))
