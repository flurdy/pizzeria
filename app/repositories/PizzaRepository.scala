package repositories

import anorm._
import anorm.SqlParser._
import com.google.inject.ImplementedBy
import javax.inject.Inject
import play.api.db._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import models._


@ImplementedBy(classOf[DefaultPizzaRepository])
trait PizzaRepository {

   def dbApi: DBApi

   private lazy val db: Database = dbApi.database("default")

   // def findPizzas() = Future{ Seq(Pizza("Hawaii"), Pizza("Pepperoni")) }

   val pizzaParser: RowParser[Pizza] = Macro.namedParser[Pizza]

   def findPizzas(): Future[List[Pizza]] = Future{
      db.withConnection { implicit conenction =>
         SQL"""
               select name from pizza order by name
            """
             .as( pizzaParser.* )
      }
   }

   // def findPizzaOrder(orderId: Long): Future[Option[PizzaOrder]] = Future {
   //    Some(PizzaOrder(Some(orderId),Pizza("Hawaii")))
   // }

   def findPizzaOrder(orderId: Long): Future[Option[PizzaOrder]] = Future {
      db.withConnection { implicit conenction =>
         SQL"""
             select p.id as pizza_id, p.name as pizza_name
             from pizza_order o
             inner join pizza p on p.id = o.pizza_id
             where o.id = $orderId
            """
          .as( ( get[Long]("pizza_id") ~
                 get[String]("pizza_name")
             ).singleOpt )
          .map{ case pizzaId ~ pizzaName =>
            PizzaOrder( Some(orderId), Pizza(pizzaName) )
          }
      }
   }

   // def addPizzaOrder(pizzaOrder: PizzaOrder): Future[PizzaOrder] = Future {
   //    pizzaOrder.copy(id = Some(123L))
   // }

   def addPizzaOrder(pizzaOrder: PizzaOrder): Future[PizzaOrder] = {

      def findPizzaId(pizzaName: String): Future[Long] = Future {
         db.withConnection { implicit connection =>
            SQL"""
                  select id from pizza where name = $pizzaName
               """
               .as( scalar[Long].single )
         }
      }

      def addOrder(pizzaId: Long): Future[Option[Long]] = Future {
         db.withConnection { implicit connection =>
            SQL"""
                  insert into pizza_order(pizza_id) values ($pizzaId)
               """
               .executeInsert()
         }
      }

      for {
         pizzaId <- findPizzaId(pizzaOrder.pizza.name)
         orderId <- addOrder(pizzaId)
      } yield pizzaOrder.copy( id = orderId )
   }
}

class DefaultPizzaRepository @Inject() (val dbApi: DBApi) extends PizzaRepository
