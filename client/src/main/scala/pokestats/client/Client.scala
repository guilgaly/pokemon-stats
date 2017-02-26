package pokestats.client

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import routerx._
import org.scalajs.dom
import pokestats.model.PokemonSummary
import rx._

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.JSApp
import scalatags.JsDom.all._
import scalatags.rx.all._
import UrlPartAdapters._

sealed trait Screen
case object PokemonListScreen extends Screen
case class PokemonDetailsScreen(pokemon: PokemonSummary) extends Screen

object UrlPartAdapters {
  implicit val pokemonDetailsUrlPart = new UrlPart[PokemonSummary] {
    override val size: Int = 2

    override def toParts(inp: PokemonSummary): List[String] =
      List(inp.id.toString, inp.name)

    override def fromParts(parts: List[String])(
        implicit ec: ExecutionContext): Future[PokemonSummary] = parts match {
      case pokeId :: pokeName :: Nil =>
        Future.successful(PokemonSummary(pokeId.toInt, pokeName))
      case pokeId :: Nil => Future.successful(PokemonSummary(pokeId.toInt, ""))
      case _ => Future.failed(new IllegalArgumentException)
    }
  }
}

object Client extends JSApp with routerx.implicits.Defaults {

  private implicit val owner = rx.Ctx.Owner.safe()

  val router: Router[Screen] = Router.generate[Screen](PokemonListScreen)

  private lazy val current: Rx[dom.Element] = Rx {
    router.current() match {
      case PokemonListScreen => screens.PokemonList.screen().apply()
      case PokemonDetailsScreen(pokemon) =>
        screens.PokemonDisplay(pokemon).screen().apply()
    }
  }

  lazy val view = {
    div(current)
  }

  override def main(): Unit = {
    router.parseUrl(dom.window.location.pathname).foreach { (screen: Screen) =>
      router.goto(screen)
      dom.document.body.appendChild(view.render)
    }
  }
}
