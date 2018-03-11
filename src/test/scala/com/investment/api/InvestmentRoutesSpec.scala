package com.investment.api

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }

class InvestmentRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with InvestmentRoutes {

  override val investmentRegistryActor: ActorRef =
    system.actorOf(InvestmentRegistryActor.props, "investmentRegistry")

  lazy val routes = investmentRoutes

  "InvestmentRoutes" should {
    "return no investment if no present (GET /investments)" in {
      val request = HttpRequest(uri = "/investments")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        println(s"returned investments  =${entityAs[String]}")
        entityAs[String] should ===("""{"investments":[]}""")
      }
    }
    "be able to add investments (POST /investments)" in {
      val investment = Investment("Kapi", 42, "jp")
      val investmentEntity = Marshal(investment).to[MessageEntity].futureValue // futureValue is from ScalaFutures
      val request = Post("/investments").withEntity(investmentEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"Investment Kapi created."}""")
      }
    }

    "be able to remove investments (DELETE /investments)" in {
      val request = Delete(uri = "/investments/Kapi")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"Investment Kapi deleted."}""")
      }
    }
  }
}

