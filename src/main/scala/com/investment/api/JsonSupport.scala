package com.investment.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.investment.api.InvestmentRegistryActor.ActionPerformed
import com.investment.domain.InvestmentDomain.{ CoinChange, CoinStats, Investment, InvestmentsResponse }
import spray.json.{ DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat }

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val investmentJsonFormat: RootJsonFormat[Investment] = jsonFormat2(Investment)
  implicit val investmentsJsonFormat: RootJsonFormat[Investments] = jsonFormat1(Investments)
  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed)

  implicit val coinChangeJsonFormat: RootJsonFormat[CoinChange] = new RootJsonFormat[CoinChange] {
    def write(c: CoinChange): JsValue = JsObject(
      "24h" -> JsNumber(c.twentyFourHour),
      "1h" -> JsNumber(c.oneHour),
      "7d" -> JsNumber(c.sevenDays)
    )

    def read(value: JsValue): CoinChange = value.asJsObject().getFields("24h", "1h", "7d") match {
      case Seq(JsString(twentyFourHour), JsString(oneHour), JsString(sevenDays)) =>
        CoinChange(twentyFourHour.toDouble, oneHour.toDouble, sevenDays.toDouble)
      case _ => throw DeserializationException("CoinChange expected")
    }
  }

  implicit val coinStatsJsonFormat: RootJsonFormat[CoinStats] = new RootJsonFormat[CoinStats] {
    def write(c: CoinStats): JsValue = JsObject(
      "market_cap" -> JsNumber(c.marketCap),
      "change" -> coinChangeJsonFormat.write(c.change),
      "24h_volume" -> JsNumber(c.twentyFourHourVolume)
    )

    def read(value: JsValue): CoinStats = value.asJsObject().getFields("market_cap", "change", "24h_volume") match {
      case Seq(JsNumber(marketCap), change, JsNumber(twentyFourHourVolume)) => {
        CoinStats(marketCap.toLong, change.convertTo[CoinChange], twentyFourHourVolume.toLong)
      }

      case _ => throw DeserializationException("CoinStats expected")
    }
  }

  implicit val investmentsResponseFormat: RootJsonFormat[InvestmentsResponse] = jsonFormat1(InvestmentsResponse)

}
