import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.sbt.PlayImport

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "pokemon-stats"

  /** The version of your application */
  val version = "1.0.0-SNAPSHOT"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.8"
    val autowire = "0.2.6"
    val uPickle = "0.4.4"
    val uTest = "0.4.4"
    val typesafeConfigGuice = "0.0.3"
    val scalajsScripts = "1.1.0"
    val twitter4s = "5.0"

    val scalaDom = "0.9.1"
    val scalatags = "0.6.3"
    val scalarx = "0.3.2"
    val scalatagsRx = "0.3.0"
    val routerx = "1.1.2"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.autowire,
    "com.lihaoyi" %%% "upickle" % versions.uPickle
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
    "com.lihaoyi" %% "utest" % versions.uTest % Test,
    "com.github.racc" % "typesafeconfig-guice" % versions.typesafeConfigGuice,
    PlayImport.guice,
    PlayImport.ws,
    PlayImport.cache,
    "com.danielasfregola" %% "twitter4s" % versions.twitter4s
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "com.lihaoyi" %%% "scalarx" % versions.scalarx,
    "com.lihaoyi" %%% "scalatags" % versions.scalatags,
    "com.timushev" %%% "scalatags-rx" % versions.scalatagsRx,
    "com.stabletechs" %%% "routerx" % versions.routerx,
    "com.lihaoyi" %%% "utest" % versions.uTest % Test
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
  ))
}
