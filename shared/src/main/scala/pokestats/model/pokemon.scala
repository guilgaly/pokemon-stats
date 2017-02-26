package pokestats.model

case class PokemonSummary(id: Int, name: String)

case class PokemonDetails(
    id: Int,
    name: String,
    sprite: String,
    height: Int,
    weight: Int,
    types: Seq[PokemonType],
    stats: Seq[PokemonStat])

case class PokemonType(id: Int, name: String)

case class PokemonStat(id: Int, name: String, value: Int)

case class TypeStats(
    typeId: Int,
    averageStats: Map[Int, Int])
