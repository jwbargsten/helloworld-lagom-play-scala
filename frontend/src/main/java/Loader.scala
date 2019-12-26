import com.example.helloworld.api.HelloWorldService
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.api.{LagomConfigComponent, ServiceAcl, ServiceInfo, ServiceLocator}
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.softwaremill.macwire.wire
import controllers.{AssetsComponents, HomeController}
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Mode}
import play.filters.HttpFiltersComponents
import router.Routes

import scala.collection.immutable
import scala.concurrent.ExecutionContext

abstract class Frontend(context: Context) extends BuiltInComponentsFromContext(context)
  with I18nComponents
  with AhcWSComponents
  with HttpFiltersComponents
  with LagomConfigComponent
  with AssetsComponents
  with LagomServiceClientComponents {

  override lazy val serviceInfo: ServiceInfo = ServiceInfo(
    "frontend",
    Map("frontend" -> immutable.Seq(ServiceAcl.forPathRegex("(?!/api/).*")))
  )
  override implicit lazy val executionContext: ExecutionContext = actorSystem.dispatcher

  override lazy val router: Routes = {
    val prefix = "/"
    wire[Routes]
  }
  lazy val helloWorldService: HelloWorldService = serviceClient.implement[HelloWorldService]

  lazy val homeController: HomeController = wire[HomeController]
}

class FrontendLoader extends ApplicationLoader {
  override def load(context: Context) = context.environment.mode match {
    case Mode.Dev =>
      (new Frontend(context) with LagomDevModeComponents).application
    case _ => (new Frontend(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }).application
  }
}
