package pokestats.pokeapi.model

case class NamedApiResource(url: String, name: String) {
  def id: Int = {
    val lastToken = url.split('/').last
    lastToken.toInt // NumberFormatException should never happen with valid data
  }
}

case class NamedApiResourceList(
    count: Int,
    previous: Option[String],
    next: Option[String],
    results: Seq[NamedApiResource])

case class VersionGameIndex(gameIndex: Int, version: NamedApiResource)

case class GenerationGameIndex(gameIndex: Int, generation: NamedApiResource)

case class Name(
    name: String,
    language: NamedApiResource
)
