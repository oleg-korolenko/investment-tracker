package com.investment.api

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.investment.api.InvestmentRegistryActor._

import scala.concurrent.Future
import scala.concurrent.duration._

//#investments-routes-class
trait InvestmentRoutes extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[InvestmentRoutes])

  def investmentRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  lazy val investmentRoutes: Route =
    pathPrefix("investments") {
      concat(
        pathEnd {
          concat(
            get {
              val investments: Future[Investments] =
                (investmentRegistryActor ? GetInvestments).mapTo[Investments]
              complete(investments)
            },
            post {
              entity(as[Investment]) { user =>
                val investmentCreated: Future[ActionPerformed] =
                  (investmentRegistryActor ? CreateInvestment(user)).mapTo[ActionPerformed]
                onSuccess(investmentCreated) { performed =>
                  log.info("Created Investment [{}]: {}", user.name, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { name =>
          concat(
            get {
              val maybeInvestment: Future[Option[Investment]] =
                (investmentRegistryActor ? GetInvestment(name)).mapTo[Option[Investment]]
              rejectEmptyResponse {
                complete(maybeInvestment)
              }
            },
            delete {
              val investmentDeleted: Future[ActionPerformed] =
                (investmentRegistryActor ? DeleteInvestment(name)).mapTo[ActionPerformed]
              onSuccess(investmentDeleted) { performed =>
                log.info("Deleted investment [{}]: {}", name, performed.description)
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
}
