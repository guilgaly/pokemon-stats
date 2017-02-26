package pokestats.services

import javax.inject.Inject

import pokestats.Api
import pokestats.model._
import pokestats.pokeapi.{PokeApi, model => apim}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class ApiService @Inject()(pokeApi: PokeApi)(
    implicit context: ExecutionContext)
    extends Api {

  private val defaultSprite =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png"

  override def listPokemons(): Future[Seq[PokemonSummary]] =
    pokeApi.listPokemons.map(toPokemonSummaries)

  override def getPokemon(id: Int): Future[PokemonDetails] =
    pokeApi.getPokemon(id).map(toPokemonDetails)

  override def getPokemons(ids: Seq[Int]): Future[Seq[PokemonDetails]] = {
    val futures = ids.map(id => getPokemon(id))
    Future.sequence(futures)
  }

  override def getPokemonsByType(id: Int): Future[Seq[PokemonDetails]] = {
    val apiType = pokeApi.getType(id)
    apiType.flatMap(t =>
      Future.sequence(t.pokemon.map(p => getPokemon(p.pokemon.id))))
  }

  override def getTypeStats(id: Int): Future[TypeStats] = {
    val apiType = pokeApi.getType(id)
    val pokemons = apiType.flatMap(t =>
      Future.sequence(t.pokemon.map(p => getPokemon(p.pokemon.id))))
    pokemons.map { ps =>
      val stats = ps.flatMap(_.stats)
      val statsMap = stats.groupBy(_.id)
      val averageStats = statsMap.mapValues(s => s.map(_.value).sum / s.size)
      TypeStats(id, averageStats)
    }
  }

//  override def getTypeStats(id: Int): Future[TypeStats] = {
//    def getPokemonsForType(t: apim.Type) = {
//      val pokemonIds = t.pokemon.map(p => p.pokemon.id)
//      getPokemons(pokemonIds)
//    }
//    def averagePokemonStats(pokemons: Seq[PokemonDetails]) = {
//      val size = pokemons.size
//      val stats = mutable.Map[Int, ArrayBuffer[PokemonStat]]()
//      for (p <- pokemons) {
//        for (s <- p.stats) {
//          if (!stats.contains(s.id)) {
//            stats += (s.id -> ArrayBuffer())
//          }
//          stats(s.id) += s
//        }
//      }
//      val reducedStats = stats.values.map(statsBuffer =>
//        statsBuffer.reduce((l, r) => l.copy(value = l.value + r.value)))
//      val averagedStats = reducedStats.map(s => s.copy(value = s.value / size))
//      averagedStats.toSeq
//    }
//    val apiType = pokeApi.getType(id)
//    val apiPokemons =
//      apiType.flatMap(t => getPokemonsForType(t).map(details => (t, details)))
//    val averageStats = apiPokemons.map {
//      case (t, pokemons) => (t, averagePokemonStats(pokemons))
//    }
//    averageStats.map { case (t, stats) => TypeStats(t.id, t.name, stats) }
//  }

  override def getTypesStats(ids: Seq[Int]): Future[Seq[TypeStats]] = {
    val futures = ids.map(id => getTypeStats(id))
    Future.sequence(futures)
  }

  private def toPokemonSummaries(
      apiPokemons: apim.NamedApiResourceList): Seq[PokemonSummary] =
    apiPokemons.results.map(apiP => PokemonSummary(apiP.id, apiP.name))

  private def toPokemonDetails(apiPokemon: apim.Pokemon): PokemonDetails =
    PokemonDetails(
      id = apiPokemon.id,
      name = apiPokemon.name,
      sprite = apiPokemon.sprites.frontDefault.getOrElse(defaultSprite),
      height = apiPokemon.height,
      weight = apiPokemon.weight,
      types = apiPokemon.types.map(apiT =>
        PokemonType(apiT.`type`.id, apiT.`type`.name)),
      stats = apiPokemon.stats.map(apiS =>
        PokemonStat(apiS.stat.id, apiS.stat.name, apiS.baseStat))
    )
}
