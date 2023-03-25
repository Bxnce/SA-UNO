package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

import Card._

case class CardStack(cards: Map[Card, Int]) {

  def alter_cs(x: Card): ((Int, Int) => Int) => CardStack = (op: (Int, Int) => Int) =>
    copy(cards.updated(x, op(cards.getOrElse(x, 2), 1)))

  override def toString: String =
    val cardstack_string: String = cards.map { case (k, v) => "(" + k.toString + "," + v.toString + ") " }.mkString
    cardstack_string
}
