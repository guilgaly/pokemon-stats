package pokestats.controllers

import javax.inject.Inject

import play.api.mvc._
import pokestats.Api
import pokestats.pokeapi.PokeApi

import scala.concurrent.ExecutionContext

class Test @Inject()(action: DefaultActionBuilder, api: Api)(implicit context: ExecutionContext)
    extends Controller {
  def test = action.async {
//    val pokeType = pokeApi.getType(1)
//    pokeType.map { p =>
//      Ok(p.toString)
//    }

//    val pokemons = api.getPokemonsByType(18)
//    pokemons.map { p=>
//      Ok(p.toString())
//    }


    val typeStats = api.getTypeStats(18)
    typeStats.map { s =>
      Ok(s.toString)
    }
  }
}
