package pokestats.services

import javax.inject.Inject

import play.api.cache.CacheApi
import pokestats.Api
import pokestats.model._
import pokestats.pokeapi.{PokeApi, model => apim}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class ApiService @Inject()(pokeApi: PokeApi, cache: CacheApi)(
    implicit context: ExecutionContext)
    extends Api {

  private def cacheKey(str: String) =
    s"pokestats.services.api.$str"

  private val defaultSprite =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png"

  override def listPokemons(): Future[Seq[PokemonSummary]] =
    withCaching(cacheKey("listPokemons")) {
      pokeApi.listPokemons.map(toPokemonSummaries)
    }

  override def getPokemon(id: Int): Future[PokemonDetails] =
    withCaching(cacheKey(s"getPokemon.$id")) {
      pokeApi.getPokemon(id).map(toPokemonDetails)
    }

  override def getPokemons(ids: Seq[Int]): Future[Seq[PokemonDetails]] = {
    val futures = ids.map(id => getPokemon(id))
    Future.sequence(futures)
  }

  override def getTypeStats(id: Int): Future[TypeStats] =
    withCaching(cacheKey(s"getTypeStats.$id")) {
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

  override def getTypesStats(ids: Seq[Int]): Future[Seq[TypeStats]] = {
    val futures = ids.map(id => getTypeStats(id))
    Future.sequence(futures)
  }

  private def getPokemonsByType(id: Int): Future[Seq[PokemonDetails]] = {
    val apiType = pokeApi.getType(id)
    apiType.flatMap(t =>
      Future.sequence(t.pokemon.map(p => getPokemon(p.pokemon.id))))
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

  private def withCaching[A: ClassTag](
      key: String,
      expiration: Duration = Duration.Inf)(orElse: => Future[A]): Future[A] = {
    cache.get[A](key).map(Future.successful).getOrElse {
      val value = orElse
      value onSuccess { case result => cache.set(key, result, expiration) }
      value
    }
  }
}
