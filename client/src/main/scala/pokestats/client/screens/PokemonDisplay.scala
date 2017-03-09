package pokestats.client.screens

import Common._
import autowire._
import org.scalajs.dom
import org.scalajs.dom.html.Div
import pokestats.Api
import pokestats.client.Ajaxer
import pokestats.model.{PokemonDetails, PokemonStat, PokemonSummary, TypeStats}
import rx._

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
  private val typeStats = Var(Seq[TypeStats]())
  private val relatedTweets = Var(Seq[String]())

  def screen()(implicit ctx: Ctx.Owner): Rx[dom.Element] = {

    val pokemonData = Rx {
      displayPokemon(pokemon(), typeStats(), relatedTweets()).render
    }

    loadPokemon()
    loadRelatedTweets()
    Rx {
      div(pokemonData).render
    }
  }

  private def loadPokemon(): Unit = {
    Ajaxer[Api].getPokemon(pokemonSummary.id).call().foreach { p =>
      pokemon() = Some(p)
      loadTypeStats(p)
    }
  }

  private def loadTypeStats(poke: PokemonDetails): Unit = {
    Ajaxer[Api].getTypesStats(poke.types.map(_.id)).call().foreach { t =>
      typeStats() = t
    }
  }

  private def loadRelatedTweets(): Unit = {
    Ajaxer[Api].getRelatedTweets(pokemonSummary.name).call().foreach { t =>
      relatedTweets() = t
    }
  }

  private def displayPokemon(
      maybeDetails: Option[PokemonDetails],
      stats: Seq[TypeStats],
      tweets: Seq[String]) = {
    maybeDetails match {
      case Some(poke) => displayFromDetails(poke, stats, tweets)
      case None => displayFromSummary()
    }
  }

  private def displayFromSummary() = {
    page(
      row(h2(pokemonSummary.name)))
  }

  private def displayFromDetails(
      poke: PokemonDetails,
      stats: Seq[TypeStats],
      tweets: Seq[String]) = {
    page(
      nameHeader(poke),
      basicInfo(poke),
      statsTable(poke, stats),
      tweetsList(poke.name, tweets)
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
    def statRow(stat: PokemonStat) = {
      tr(
        td(strong(stat.name)),
        td(stat.value),
        for {
          pokeType <- poke.types
          typeStats = stats.find(_.typeId == pokeType.id)
          typeStat = typeStats
            .flatMap(_.averageStats.get(stat.id))
            .getOrElse(0)
        } yield td(typeStat.toString)
      )
    }
    row(
      h3("Stats"),
      table(cls := "u-full-width")(
        thead(
          tr(
            th("Stat"),
            th("Value"),
            for {
              pokeType <- poke.types
            } yield th(pokeType.name, " avg.")
          )),
        tbody(
          for {
            stat <- poke.stats
          } yield statRow(stat)
        )
      )
    )
  }
  private def tweetsList(pokemonName: String, tweets: Seq[String]) = {
    row(
      h3(s"Tweets about #$pokemonName"),
      table(
        tbody(
          for {
            tweet <- tweets
          } yield tr(td(tweet))
        )
      )
    )
  }
}
