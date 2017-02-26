package pokestats.client.screens

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._
import pokestats.client._

import scalatags.JsDom.TypedTag

object Common {

  val v: HtmlTag = a(href := "javascript:void(0)")

  def row: HtmlTag = div(cls := "row")

  def columns(columns: Columns): HtmlTag = div(cls := columns.cssClass)

  lazy val nav: HtmlTag = row(
    columns(TwoColumns)(v("All Pokémons", onclick := { () =>
      Client.router.goto(PokemonListScreen)
    }))
  )

  def page(rows: HtmlTag*): HtmlTag = div(cls := "container")(nav, rows)

  sealed abstract class Columns(val cssClass: String)
  case object OneColumn extends Columns("one column")
  case object TwoColumns extends Columns("two columns")
  case object ThreeColumns extends Columns("three columns")
  case object FourColumns extends Columns("four columns")
  case object FiveColumns extends Columns("five columns")
  case object SixColumns extends Columns("six columns")
  case object SevenColumns extends Columns("seven columns")
  case object EightColumns extends Columns("eight columns")
  case object NineColumns extends Columns("nine columns")
  case object TenColumns extends Columns("ten columns")
  case object ElevenColumns extends Columns("eleven columns")
}
