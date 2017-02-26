package pokestats.controllers

import java.nio.ByteBuffer

import pokestats.UPickleSerializers.pickler
import javax.inject.Inject

import akka.util.ByteString
import play.api.{Configuration, Environment}
import play.api.http.HttpEntity
import play.api.mvc._
import pokestats.{Api, UPickleSerializers}

import scala.concurrent.ExecutionContext

object Router
    extends UPickleSerializers
    with autowire.Server[String, pickler.Reader, pickler.Writer]

class Application @Inject()(api: Api)(
    implicit context: ExecutionContext,
    config: Configuration,
    env: Environment)
    extends Controller {

  def index(path: String) = Action {
    Ok(views.html.pokestats.index("PokéStats"))
  }

  def autowireApi(path: String) = Action.async(parse.tolerantText) {
    implicit request =>
      println(s"Request path: $path")

      // get the request body as String
      val b = request.body

      // call Autowire route
      Router
        .route[Api](api)(
          autowire.Core
            .Request(path.split("/"), pickler.read[Map[String, String]](b))
        )
        .map(result => {
          Result(
            header = ResponseHeader(200, Map.empty),
            body =
              HttpEntity.Strict(ByteString(result), Some("application/json"))
          )
        })
  }
}
