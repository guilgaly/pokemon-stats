package pokestats

import UPickleSerializers.pickler

object UPickleSerializers {
  val pickler = upickle.default
}

abstract class UPickleSerializers extends autowire.Serializers[String, pickler.Reader, pickler.Writer] {

  override def read[Result: pickler.Reader](p: String): Result =
    pickler.read[Result](p)

  override def write[Result: pickler.Writer](r: Result): String =
    pickler.write[Result](r)
}
