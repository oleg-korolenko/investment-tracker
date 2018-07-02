package com.investment.domain

/**
 * Created by okorolenko on 18/03/2018.
 */
object InvestmentDomain {

  final case class Investment(coin: String, amount: Double)

  case class CoinChange(twentyFourHour: Double, oneHour: Double, sevenDays: Double)

  case class CoinStats(marketCap: Long, change: CoinChange, twentyFourHourVolume: Long)

  case class InvestmentsResponse(investments: List[Investment])

  /*sealed trait Currency
  final case object ETH extends Currency
  final case object BTC extends Currency


  implicit object CurrencyJsonReader extends JsonReader[Currency] {
    def read(value: JsValue): Currency = value match {
      case JsString("ETH") => ETH
      case JsString("BTC") => BTC
    }
  }
*/
}
