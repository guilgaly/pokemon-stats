package pokestats.client.screens

import Common._
import autowire._
import org.scalajs.dom
import org.scalajs.dom.html.Div
import pokestats.Api
import pokestats.client.Ajaxer
import pokestats.model.{PokemonDetails, PokemonSummary, TypeStats}
import rx._
import rx.async._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import scalatags.rx.all._

object PokemonDisplay {
  def apply(pokemonSummary: PokemonSummary): PokemonDisplay =
    new PokemonDisplay(pokemonSummary)
}

class PokemonDisplay(pokemonSummary: PokemonSummary) {

  private val pokemon = Var[Option[PokemonDetails]](None)

  def screen()(implicit ctx: Ctx.Owner): Rx[dom.Element] = {

    val typeStats: Rx[Seq[TypeStats]] = Rx {
      val t = loadTypeStats(pokemon()).toRx(Seq())
      t()
    }

    val pokemonData = Rx {
      displayPokemon(pokemon(), typeStats()).render
    }

    loadPokemon()
    Rx {
      div(pokemonData).render
    }
  }

  private def loadPokemon(): Unit = {
    Ajaxer[Api].getPokemon(pokemonSummary.id).call().foreach { p =>
      pokemon() = Some(p)
    }
  }

  private def loadTypeStats(poke: Option[PokemonDetails]) = {
    poke match {
      case Some(po) => Ajaxer[Api].getTypesStats(po.types.map(_.id)).call()
      case None => Future.successful(Seq())
    }
  }

  private def displayPokemon(
      maybeDetails: Option[PokemonDetails],
      stats: Seq[TypeStats]) = {
    maybeDetails match {
      case Some(poke) => displayFromDetails(poke, stats)
      case None => displayFromSummary()
    }
  }

  private def displayFromSummary() = {
    page(row(h2(pokemonSummary.name)))
  }

  private def displayFromDetails(poke: PokemonDetails, stats: Seq[TypeStats]) = {
    page(
      nameHeader(poke),
      basicInfo(poke),
      statsTable(poke, stats)
    )
  }

  private def nameHeader(poke: PokemonDetails) = {
    row(h2(poke.name))
  }
  private def basicInfo(poke: PokemonDetails) = {
    val typ = poke.types.map(_.name).mkString(", ")
    row(
      columns(OneColumn)(img(src := poke.sprite)),
      columns(ElevenColumns)(
        // format: off
        p(
          strong("ID: "), poke.id, br,
          strong("Type(s): "), typ, br,
          strong("Height: "),
          poke.height, br, strong("Weight: "),
          poke.weight
        )
        // format: on
      )
    )
  }
  private def statsTable(poke: PokemonDetails, stats: Seq[TypeStats]) = {
    row(
      h3("Stats"),
      table(cls := "u-full-width")(
        thead(tr(th("Stat"), th("Value"))),
        tbody(
          for {
            stat <- poke.stats
          } yield tr(td(strong(stat.name)), td(stat.value))
        )
      ),
      p("Stats: ", stats.size)
    )
  }
}
