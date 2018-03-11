package com.investment.api

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object QuickstartServer extends App with InvestmentRoutes {

  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("investments")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //#server-bootstrapping

  val investmentRegistryActor: ActorRef = system.actorOf(InvestmentRegistryActor.props, "investmentsRegistryActor")

  lazy val routes: Route = investmentRoutes

  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
}
