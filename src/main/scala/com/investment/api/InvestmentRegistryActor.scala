package com.investment.api

import akka.actor.{ Actor, ActorLogging, Props }
import com.investment.domain.InvestmentDomain.Investment

final case class Investments(investments: Seq[Investment])

object InvestmentRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetInvestments
  final case class CreateInvestment(investment: Investment)
  final case class GetInvestment(coin: String)
  final case class DeleteInvestment(coin: String)

  def props: Props = Props[InvestmentRegistryActor]
}

class InvestmentRegistryActor extends Actor with ActorLogging {
  import InvestmentRegistryActor._

  var investments = Set.empty[Investment]

  def receive: Receive = {
    case GetInvestments =>
      sender() ! Investments(investments.toSeq)
    case CreateInvestment(investment) =>
      investments += investment
      sender() ! ActionPerformed(s"Investment ${investment.coin} created.")
    case GetInvestment(coin) =>
      sender() ! investments.find(_.coin == coin)
    case DeleteInvestment(coin) =>
      investments.find(_.coin == coin) foreach { investment => investments -= investment }
      sender() ! ActionPerformed(s"Investment ${coin} deleted.")
  }
}
