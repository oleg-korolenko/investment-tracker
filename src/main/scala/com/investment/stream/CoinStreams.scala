package com.investment.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, Uri }
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import akka.util.ByteString
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by okorolenko on 24/04/2018.
 */

trait CoinStreams {

  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer
  implicit def executionContext: ExecutionContextExecutor

  val httpClient = Http().outgoingConnectionHttps(host = "chasing-coins.com", port = 443)

  val getCoinsMarketCapStream = Source
    .tick(100 millis, 5 minutes, HttpRequest(uri = Uri("/api/v1/std/marketcap")))
    .via(httpClient)
    .mapAsync(1)(res =>
      res.entity.dataBytes
        .runWith(Sink.fold(ByteString.empty)(_ ++ _))
        .map(_.utf8String))
    .log("error logging")
    /*.recover {
      case _: StreamTcpException ⇒ "Nooooooooo"
    }*/
    .map { coin => new ProducerRecord[Array[Byte], String]("coinMarketCap", coin) }

  val getETHStatsStream = Source
    .tick(100 millis, 1 minute, HttpRequest(uri = Uri("/api/v1/std/coin/ETH")))
    .via(httpClient)
    .mapAsync(1)(res =>
      res.entity.dataBytes
        .runWith(Sink.fold(ByteString.empty)(_ ++ _))
        .map(_.utf8String))
    .log("error logging")
    /*.recover {
      case _: StreamTcpException ⇒ "Nooooooooo"
    }*/
    .map { coin => new ProducerRecord[Array[Byte], String]("coinStats", coin) }
}
