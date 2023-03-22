package de.htwg.se.uno
package model.gameComponent

import model.gameComponent.gameBaseImpl._
import util.State

trait gameInterface {
  val pList: List[Player]
  val currentstate: State
  val ERROR: Int
  val cardStack: CardStack
  val midCard: Player
  val cardsInDeck: Int
  val winner: Int

  def take(player: String): gameInterface
  def place(ind: Int, player: Int): gameInterface
  def checkWin(player: Player): Boolean
  def setError(err: Int): gameInterface
  def init(): gameInterface
  def getNext(game: gameInterface, player: Int, state: State): gameInterface
  def changeMid(tmp: gameInterface, c: Card): gameInterface
  def chooseColor(color: String): gameInterface
}
