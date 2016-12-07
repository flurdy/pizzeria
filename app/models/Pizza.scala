package models

case class Pizza(name: String)

case class PizzaOrder(id: Option[Long], pizza: Pizza)
