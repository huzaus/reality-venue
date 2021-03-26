package co.reality.domain

import io.estatico.newtype.macros.newtype

import java.util.UUID
import scala.language.implicitConversions

package object entity {

  @newtype final case class VenueId(value  : UUID)

  @newtype final case class PlayerId(value: String)

}
