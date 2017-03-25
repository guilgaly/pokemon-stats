package pokestats.util

import testutil.UnitSpec

class OptionSnakePicklerSpec extends UnitSpec with JsonEqualityMatcher {

  case class Foo(bar: Option[String])

  case class MyNestedClass(myFirstMember: Int, mySecondMember: String)
  case class MyOuterClass(myNestedClassMember: MyNestedClass)

  import pokestats.util.OptionSnakePickler._

  "An option" when {
    "empty" should {

      val jsonString = """{"bar": null}"""
      val scalaObj = Foo(None)

      "serialize to null" in {
        write(scalaObj) should equalsToJson(jsonString)
      }
      "deserialize from null" in {
        read[Foo](jsonString) should equal(scalaObj)
      }
    }
    "non empty" should {

      val jsonString = """{"bar": "Hello World!"}"""
      val scalaObj = Foo(Some("Hello World!"))

      "serialize to the wrapped value" in {
        write(scalaObj) should equalsToJson(jsonString)
      }
      "deserialize from the wrapped value" in {
        read[Foo](jsonString) should equal(scalaObj)
      }
    }
  }

  "A case class" when {
    "CamelCased" should {

      val jsonString =
        """{
          |  "my_nested_class_member": {
          |    "my_first_member": 666,
          |    "my_second_member": "toto"
          |  }
          |}""".stripMargin
      val scalaObj = MyOuterClass(MyNestedClass(666, "toto"))

      "serialize to snake_cased json" in {
        write(scalaObj) should equalsToJson(jsonString)
      }
      "deserialize from snake_cased json" in {
        read[MyOuterClass](jsonString) should equal(scalaObj)
      }
    }
  }

}
