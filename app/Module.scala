import com.google.inject.AbstractModule
import services.CountryQuerier


class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[CountryQuerier]).asEagerSingleton()
  }

}
