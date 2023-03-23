package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

import Card._

case class CardStack(cards: Map[Card, Int]) {

  def decrease(x: Card): CardStack =
    copy(cards.updated(x, cards.getOrElse(x, 2) - 1))

  def increase(x: Card): CardStack =
    copy(cards.updated(x, cards.getOrElse(x, 2) + 1))

  override def toString: String =
    val cardstack_string: String = cards.map { case (k, v) => "(" + k.toString + "," + v.toString + ") " }.mkString
    cardstack_string
}
