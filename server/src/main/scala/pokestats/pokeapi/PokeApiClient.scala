package pokestats.pokeapi

import pokestats.util.DurationConverters._
import java.time
import javax.inject.{Inject, Singleton}

import com.github.racc.tscg.TypesafeConfig
import play.api.http.HeaderNames.ACCEPT
import play.api.libs.ws.{WSClient, WSRequest}
import pokestats.pokeapi.model.{NamedApiResourceList, Pokemon, Type}
import pokestats.util.OptionSnakePickler._

import scala.concurrent.{ExecutionContext, Future}

trait PokeApi {
  def listPokemons: Future[NamedApiResourceList]
  def getPokemon(id: Int): Future[Pokemon]
  def getType(id: Int): Future[Type]
}

@Singleton
class PokeApiClient @Inject()(
    ws: WSClient,
    @TypesafeConfig("pokestats.pokeapi.rootUrl") rootUrl: String,
    @TypesafeConfig("pokestats.pokeapi.timeout") timeout: time.Duration)(
    implicit context: ExecutionContext)
    extends PokeApi {

  override def listPokemons: Future[NamedApiResourceList] =
    list[NamedApiResourceList]("pokemon")

  override def getPokemon(id: Int): Future[Pokemon] =
    get[Pokemon](s"pokemon/$id")

  override def getType(id: Int): Future[Type] =
    get[Type](s"type/$id")

  private def get[A: Reader](path: String) = {
    val req = request(path)
    getAndParse[A](req)
  }

  private def list[A: Reader](path: String) = {
    // Always get the full list by passing a very high limit
    val req = request(path).withQueryString("limit" -> "999999")
    getAndParse[A](req)
  }

  private def request(path: String) = {
    ws.url(s"$rootUrl/$path")
      .withHeaders(ACCEPT -> "application/json")
      .withRequestTimeout(timeout.asScala)
  }

  private def getAndParse[A: Reader](request: WSRequest) = {
    val response = request.get()
    response.map(r => read[A](r.body))
  }
}
