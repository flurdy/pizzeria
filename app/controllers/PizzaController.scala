package controllers

import javax.inject._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import scala.concurrent.Future
import repositories._
import models._

@Singleton
class PizzaController @Inject() (val pizzaRepository: PizzaRepository) extends Controller {

   val orderForm = Form( mapping (
         "id" -> ignored(None: Option[Long]),
         "pizza" -> mapping (
           "name" -> nonEmptyText
         )(Pizza.apply)(Pizza.unapply)
      )(PizzaOrder.apply)(PizzaOrder.unapply)
   )

   def showMenu = Action.async {
      pizzaRepository.findPizzas().map{ pizzas =>
         Ok(views.html.pizzamenu(pizzas))
      }
   }

  // def orderPizza = Action { implicit request =>
  //    orderForm.bindFromRequest.fold(
  //      errors => BadRequest,
  //      order  => Created
  //    )
  // }

  def orderPizza = Action.async { implicit request =>
      orderForm.bindFromRequest.fold(
         errors => Future.successful( BadRequest ),
         order  => {
            pizzaRepository.addPizzaOrder(order) map {
               case PizzaOrder(Some(orderId), _) =>
                  Redirect(routes.PizzaController.showConfirmation(orderId))
               case _ => InternalServerError("Could not add pizza order")
            }
         }
      )
  }

   def showConfirmation(orderId: Long) = Action.async {
      pizzaRepository.findPizzaOrder(orderId).map{
          case Some(order) => Ok(views.html.confirmation(order))
          case _           => NotFound
      }
   }
}
