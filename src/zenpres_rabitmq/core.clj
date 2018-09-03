(ns zenpres-rabitmq.core
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb])
  (:gen-class))

(def ^{:const true} default-exchange-name "")
(def conn (rmq/connect))
(def ch (lch/open conn))
(def qname "screenshot-image")

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type))
  (println meta)
  (do
    (Thread/sleep 10000)
    (doseq [i (range 10)]
      (println (str "Image will be generated X: " i)))
    (lb/ack ch delivery-tag)))

(defn -main
  [& args]
  (let [conn  (rmq/connect {:uri "amqp://saaiaidw:vV0YBbajrgsU-vHzMbscpQrCpLFMHf7U@mustang.rmq.cloudamqp.com/saaiaidw"})
        ch    (lch/open conn)
        qname "studentExamPacketAnswer"]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber ch)))
    ;(lq/declare ch qname {:exclusive false :auto-delete false})
    (lc/subscribe ch qname message-handler {:auto-ack false})
    ;(lb/publish ch default-exchange-name qname "Hello!" {:content-type "text/plain" :type "greetings.hi"})
    (Thread/sleep 2000)
    (println "[main] Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))
