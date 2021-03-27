package co.reality.domain

import cats.syntax.either._
import io.estatico.newtype.macros.newtype

import java.util.UUID
import scala.language.implicitConversions
import scala.util.Try

package object entity {

  @newtype final case class VenueId(value: UUID)

  object VenueId {
    def fromString(value: String): Either[String, VenueId] =
      Try(UUID.fromString(value)).toEither
                                 .map(uuid => VenueId(uuid))
                                 .leftMap(_.getMessage)
  }

  @newtype final case class PlayerId(value: String)

}
