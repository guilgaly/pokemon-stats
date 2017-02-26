package pokestats.util

import upickle.Js

/**
  * A [[upickle.Api]] which automatically converts attribute names between
  * camelCase (in Scala) and snake_case (in JSON).
  *
  * @see [[http://www.lihaoyi.com/upickle-pprint/upickle/#CustomConfiguration]]
  */
object OptionSnakePickler extends upickle.AttributeTagged {

  private def camelToSnake(s: String) = {
    s.split("(?=[A-Z])", -1).map(_.toLowerCase).mkString("_")
  }

  override def CaseR[T: this.Reader, V](
      f: T => V,
      names: Array[String],
      defaults: Array[Js.Value]): Reader[V] = {
    super.CaseR[T, V](f, names.map(camelToSnake), defaults)
  }

  override def CaseW[T: this.Writer, V](
      f: V => Option[T],
      names: Array[String],
      defaults: Array[Js.Value]): Writer[V] = {
    super.CaseW[T, V](f, names.map(camelToSnake), defaults)
  }

  override implicit def OptionW[T: Writer]: Writer[Option[T]] = Writer {
    case None    => Js.Null
    case Some(s) => implicitly[Writer[T]].write(s)
  }

  override implicit def OptionR[T: Reader]: Reader[Option[T]] = Reader {
    case Js.Null     => None
    case v: Js.Value => Some(implicitly[Reader[T]].read.apply(v))
  }
}
