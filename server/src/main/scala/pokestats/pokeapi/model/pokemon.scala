package pokestats.pokeapi.model

case class Pokemon(
    id: Int,
    name: String,
    baseExperience: Int,
    height: Int,
    isDefault: Boolean,
    order: Int,
    weight: Int,
    species: NamedApiResource,
    abilities: Seq[PokemonAbility],
    forms: Seq[NamedApiResource],
    gameIndices: Seq[VersionGameIndex],
    heldItems: Seq[PokemonHeldItem],
    moves: Seq[PokemonMove],
    stats: Seq[PokemonStat],
    types: Seq[PokemonType],
    sprites: PokemonSprites
)

case class PokemonAbility(
    isHidden: Boolean,
    slot: Int,
    ability: NamedApiResource)

case class PokemonHeldItem(
    item: NamedApiResource,
    versionDetails: Seq[PokemonHeldItemVersion])

case class PokemonHeldItemVersion(version: NamedApiResource, rarity: Int)

case class PokemonMove(
    move: NamedApiResource,
    versionGroupDetails: Seq[PokemonMoveVersion])

case class PokemonMoveVersion(
    moveLearnMethod: NamedApiResource,
    versionGroup: NamedApiResource,
    levelLearnedAt: Int
)

case class PokemonStat(stat: NamedApiResource, effort: Int, baseStat: Int)

case class PokemonType(slot: Int, `type`: NamedApiResource)

case class PokemonSprites(
    backDefault: Option[String],
    backShiny: Option[String],
    frontDefault: Option[String],
    frontShiny: Option[String],
    backFemale: Option[String],
    backShinyFemale: Option[String],
    frontFemale: Option[String],
    frontShinyFemale: Option[String])

case class Type(
  id: Int,
  name: String,
  damageRelations: TypeRelations,
  gameIndices: Seq[GenerationGameIndex],
  generation: NamedApiResource,
  moveDamageClass: Option[NamedApiResource],
  names: Seq[Name],
  pokemon: Seq[TypePokemon],
  moves: Seq[NamedApiResource]
)

case class TypeRelations(
  noDamageTo: Seq[NamedApiResource],
  halfDamageTo: Seq[NamedApiResource],
  doubleDamageTo: Seq[NamedApiResource],
  noDamageFrom: Seq[NamedApiResource],
  halfDamageFrom: Seq[NamedApiResource],
  doubleDamageFrom: Seq[NamedApiResource]
)

case class TypePokemon(
  slot: Int,
  pokemon: NamedApiResource
)