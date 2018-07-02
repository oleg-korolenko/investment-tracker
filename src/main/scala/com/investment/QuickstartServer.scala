package com.investment

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import com.investment.api.{ InvestmentRegistryActor, InvestmentRoutes, JsonSupport }
import com.investment.producer.{ InvestmentProducerActor, ProducingToKafka }
import com.investment.stream.CoinStreams
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.KafkaProducer

import scala.concurrent.{ Await, ExecutionContextExecutor }
import scala.concurrent.duration.Duration

object QuickstartServer extends App with JsonSupport
    with InvestmentRoutes
    with ProducingToKafka
    with CoinStreams {

  val config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem("investments", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // kafka props
  val kafkaProducer: KafkaProducer[Array[Byte], String] = producer
  val kafkaProducerSettings = producerSettings

  /**
   * Actors
   */
  val investmentRegistryActor: ActorRef = system.actorOf(InvestmentRegistryActor.props, "investmentRegistryActor")

  val investmentProducerActor: ActorRef = system.actorOf(InvestmentProducerActor.props(kafkaProducer), "investmentProducerActor")

  /**
   * Routes
   */
  lazy val routes: Route = investmentRoutes

  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")

  /**
   * Coin streams
   */

  getCoinsMarketCapStream.runWith(Producer.plainSink(producerSettings))
  getETHStatsStream.runWith(Producer.plainSink(producerSettings))

  Await.result(system.whenTerminated, Duration.Inf)
}
