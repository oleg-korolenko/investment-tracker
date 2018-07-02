package com.investment.producer

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.{ ByteArraySerializer, StringSerializer }

/**
 * Created by okorolenko on 18/03/2018.
 */
trait ProducingToKafka {
  implicit def system: ActorSystem

  lazy val producerSettings: ProducerSettings[Array[Byte], String] =
    ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")

  lazy val producer: KafkaProducer[Array[Byte], String] = {
    val kafkaProducer = producerSettings.createKafkaProducer()
    kafkaProducer
  }
}
