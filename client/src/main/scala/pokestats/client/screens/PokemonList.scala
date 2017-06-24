package pokestats.client.screens

import autowire._
import org.scalajs.dom
import pokestats.Api
import pokestats.client.screens.Common._
import pokestats.client.{Ajaxer, Client, PokemonDetailsScreen}
import pokestats.model.PokemonSummary
import rx._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.all._
import scalatags.rx.all._

object PokemonList {

  private val pokemons = Var(Seq[PokemonSummary]())
  private val searchText = Var("")

  def screen()(implicit ctx: Ctx.Owner): Rx[dom.Element] = Rx {

    // List of pokemons to display
    val pokemonsList = Rx {
      filteredPokemonsList(pokemons(), searchText())
    }

    // searchBox
    val inputBox = input.render
    inputBox.value = searchText.now
    val updateSearchText = (e: dom.Event) => searchText() = inputBox.value
    inputBox.onchange = updateSearchText
    inputBox.onkeyup = updateSearchText

    loadAllPokemons()

    page(
      row(h1("All Pokémons")),
      row(p(inputBox)),
      row(p(pokemonsList))
    ).render
  }

  private def loadAllPokemons(): Unit = {
    Ajaxer[Api].listPokemons().call().foreach { p =>
      pokemons() = p
    }
  }

  private def filteredPokemonsList(
      pokemons: Seq[PokemonSummary],
      search: String) = {
    ul(
      for {
        pokemon <- filterPokemons(pokemons, search)
        label = pokemon.name + " [" + pokemon.id + "]"
      } yield
        li(
          alink(label, onclick := { () =>
            Client.router.goto(PokemonDetailsScreen(pokemon))
          })
        )
    ).render
  }

  private def filterPokemons(pokemons: Seq[PokemonSummary], search: String) = {
    def matcher(p: PokemonSummary) =
      p.name.contains(search) || p.id.toString == search
    if (search.isEmpty) {
      pokemons
    } else {
      pokemons.filter(matcher)
    }
  }
}
