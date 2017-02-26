package pokestats

import com.google.inject.AbstractModule
import pokestats.pokeapi.{PokeApi, PokeApiClient}
import pokestats.services.ApiService

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Api]).to(classOf[ApiService])
    bind(classOf[PokeApi]).to(classOf[PokeApiClient])
  }
}
