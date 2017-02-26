package pokestats.client

import org.scalajs.dom.ext.Ajax
import pokestats.UPickleSerializers
import pokestats.UPickleSerializers.pickler

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Ajaxer
  extends UPickleSerializers
    with autowire.Client[String, pickler.Reader, pickler.Writer] {

  override def doCall(req: Request): Future[String] = {
    Ajax
      .post(
        url = "/api/" + req.path.mkString("/"),
        data = pickler.write(req.args)
      )
      .map(_.responseText)
  }
}
