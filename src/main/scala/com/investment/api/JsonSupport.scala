package com.investment.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.investment.api.InvestmentRegistryActor.ActionPerformed
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val investmentJsonFormat = jsonFormat3(Investment)
  implicit val investmentsJsonFormat = jsonFormat1(Investments)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
