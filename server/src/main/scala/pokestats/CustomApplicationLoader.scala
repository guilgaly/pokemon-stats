package pokestats

import com.github.racc.tscg.TypesafeConfigModule
import play.api.ApplicationLoader.Context
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}

class CustomApplicationLoader extends GuiceApplicationLoader {
  override protected def builder(context: Context): GuiceApplicationBuilder = {
    val config = context.initialConfiguration.underlying
    val configModule = TypesafeConfigModule.fromConfigWithPackage(config, "pokestats")

    super.builder(context).bindings(configModule)
  }
}
