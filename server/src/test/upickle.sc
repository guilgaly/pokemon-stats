import pokestats.pokeapi.model.PokemonSprites
import pokestats.util.OptionSnakePickler._

val p = PokemonSprites(
  backDefault = None,
  backShiny = None,
  frontDefault = Some("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
  frontShiny = None,
  backFemale = None,
  backShinyFemale = None,
  frontFemale = None,
  frontShinyFemale = None
)

val pickled = write(p)

val unpickled = read[PokemonSprites](pickled)
