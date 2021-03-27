package co.reality.api.util

import cats.syntax.semigroupk._
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.clock.Clock
import zio.interop.catz._
import zio.{RIO, ULayer, ZIO}

object ZioRoutes {

  type ZioHttpRoutes = HttpRoutes[RIO[Clock, *]]

  def apply[Input, Error, Response, Env](
                                          definition: ZEndpoint[Input, Error, Response],
                                          handler   : Input => ZIO[Env, Error, Response],
                                          layer     : ULayer[Env]
                                        ): ZioHttpRoutes =
    ZHttp4sServerInterpreter.from(definition.zServerLogic[Any](handler(_).provideLayer(layer))).toRoutes

  def app(routes: ZioHttpRoutes): HttpApp[RIO[Clock, *]] = Router("/" -> routes).orNotFound

  def apply(first: ZioHttpRoutes, second: ZioHttpRoutes, rest: ZioHttpRoutes*): ZioHttpRoutes =
    apply(first +: second +: rest)

  def apply(routes: Seq[ZioHttpRoutes]): ZioHttpRoutes = routes.reduce(_ <+> _)

}
