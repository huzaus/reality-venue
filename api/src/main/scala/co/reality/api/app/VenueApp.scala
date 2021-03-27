package co.reality.api.app

import co.reality.api.route.VenueRoutes
import co.reality.domain.input.VenueModule
import co.reality.domain.input.VenueModule.VenueService
import org.http4s.server.blaze.BlazeServerBuilder
import zio.clock.Clock
import zio.interop.catz._
import zio.{App, ExitCode, RIO, Runtime, ULayer, URIO, ZEnv}

import scala.concurrent.duration.Duration

object VenueApp extends App {
  implicit val runtime: Runtime[ZEnv] = this

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    BlazeServerBuilder[RIO[Clock, *]](runtime.platform.executor.asEC)
      .withHttpApp(VenueRoutes.app(layer))
      .withIdleTimeout(Duration.Inf)
      .serve
      .compile
      .drain
      .exitCode
  }

  val layer: ULayer[VenueService] = VenueModule.inMemoryLayer
}
