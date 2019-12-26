package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult
import com.example.helloworld.api.HelloWorldService
import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, helloWorldService: HelloWorldService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    lazy val x = helloWorldService.hello("wrust") invoke()
    x.map {s => Ok(s + "wurst3")}
  }
}
