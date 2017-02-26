package pokestats

import pokestats.model.{PokemonDetails, PokemonSummary, TypeStats}

import scala.concurrent.Future

trait Api {
  def listPokemons(): Future[Seq[PokemonSummary]]
  def getPokemon(id: Int): Future[PokemonDetails]
  def getPokemons(ids: Seq[Int]): Future[Seq[PokemonDetails]]
  def getTypeStats(id: Int): Future[TypeStats]
  def getTypesStats(ids: Seq[Int]): Future[Seq[TypeStats]]
}
