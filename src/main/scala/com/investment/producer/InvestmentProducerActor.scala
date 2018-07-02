package com.investment.producer

import akka.actor.{ Actor, ActorLogging, Props }
import com.investment.api.InvestmentRegistryActor.ActionPerformed
import com.investment.domain.InvestmentDomain._
import com.investment.producer.InvestmentProducerActor.SaveInvestment
import org.apache.kafka.clients.producer.KafkaProducer

/**
 * Created by okorolenko on 18/03/2018.
 */

object InvestmentProducerActor {

  final case class SaveInvestment(investment: Investment)

  def props(kafkaProducer: KafkaProducer[Array[Byte], String]): Props = Props(new InvestmentProducerActor(kafkaProducer))
}

class InvestmentProducerActor(kafkaProducer: KafkaProducer[Array[Byte], String]) extends Actor with ActorLogging {

  override def receive: Receive = {
    case SaveInvestment(investment) => {
      // TODO
      sender() ! ActionPerformed(s"Investment ${investment.coin} created.")
    }
  }
}
