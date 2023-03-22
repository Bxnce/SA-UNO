package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

//import scala.collection.immutable.HashMap   //funktioniert auch ohne den Import
import Card._

case class CardStack(cards: Map[Card, Int]) {

  def decrease(x: Card): CardStack =
    copy(cards.updated(x, cards.getOrElse(x, 2) - 1))

  def increase(x: Card): CardStack =
    copy(cards.updated(x, cards.getOrElse(x, 2) + 1))

  override def toString: String =
    var tmp = ""
    for ((k, v) <- cards) tmp = tmp + k.toString + "," + v.toString + " "

    tmp
}
