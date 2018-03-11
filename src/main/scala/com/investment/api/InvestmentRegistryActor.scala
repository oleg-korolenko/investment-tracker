package com.investment.api

import akka.actor.{ Actor, ActorLogging, Props }

final case class Investment(name: String, age: Int, countryOfResidence: String)
final case class Investments(investments: Seq[Investment])

object InvestmentRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetInvestments
  final case class CreateInvestment(investment: Investment)
  final case class GetInvestment(name: String)
  final case class DeleteInvestment(name: String)

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
      sender() ! ActionPerformed(s"Investment ${investment.name} created.")
    case GetInvestment(name) =>
      sender() ! investments.find(_.name == name)
    case DeleteInvestment(name) =>
      investments.find(_.name == name) foreach { investment => investments -= investment }
      sender() ! ActionPerformed(s"Investment ${name} deleted.")
  }
}
