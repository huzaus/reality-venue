package co.reality.api.route

import cats.syntax.either._
import co.reality.api.route.VenueRoutes.context
import co.reality.api.view.{BuyVenueRequest, PutVenueRequest, Venue}
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe._
import org.scalatest.EitherValues
import zio.RIO
import zio.clock.Clock
import zio.interop.catz._

trait VenueBehaviours {

  def app: HttpApp[RIO[Clock, *]]

  def buy(id: String, body: BuyVenueRequest): RIO[Clock, Either[String, String]] = {
    val request: Request[RIO[Clock, *]] =
      Request(method = Method.POST)
        .withUri(Uri(path = s"/$context/$id"))
        .withEntity(body.asJson)
    for {
      response <- app.run(request)
      entity <- response.status match {
        case Status.Ok => response.bodyText.compile.string.map(_.asRight)
        case _         => response.bodyText.compile.string.map(_.asLeft)
      }
    } yield entity
  }

  def put(id: String, body: PutVenueRequest): RIO[Clock, Either[String, String]] = {
    val request: Request[RIO[Clock, *]] =
      Request(method = Method.PUT)
        .withUri(Uri(path = s"/$context/$id"))
        .withEntity(body.asJson)
    for {
      response <- app.run(request)
      entity <- response.status match {
        case Status.Ok => response.bodyText.compile.string.map(_.asRight)
        case _         => response.bodyText.compile.string.map(_.asLeft)
      }
    } yield entity
  }

  def delete(id: String): RIO[Clock, Either[String, String]] = {
    val request: Request[RIO[Clock, *]] =
      Request(method = Method.DELETE)
        .withUri(Uri(path = s"/$context/$id"))
    for {
      response <- app.run(request)
      entity <- response.status match {
        case Status.Ok => response.bodyText.compile.string.map(_.asRight)
        case _         => response.bodyText.compile.string.map(_.asLeft)
      }
    } yield entity
  }

  def getAll(): RIO[Clock, Either[String, Vector[Venue]]] = {
    val request: Request[RIO[Clock, *]] =
      Request(method = Method.GET)
        .withUri(Uri(path = s"/$context"))
    for {
      response <- app.run(request)
      entity <- response.status match {
        case Status.Ok => response.as[Vector[Venue]].map(_.asRight)
        case _         => response.bodyText.compile.string.map(_.asLeft)
      }
    } yield entity
  }

  implicit class Response[E, T](response: Either[E, T]) extends EitherValues {
    def success: T = response.value

    def failure: E = response.left.value
  }
}
