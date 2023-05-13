package fileIOComponent.database

import model.gameComponent.gameInterface

import scala.util.Try

trait DAOInterface {
  def save(game: gameInterface): Unit

  def load(id: Option[Int]): Try[gameInterface]
  
  def updateGame(id: Int, player1: Option[Int], player2: Option[Int], midCard: Option[Int], currentstate: Option[String], error: Option[Int], cardstack: Option[String], winner: Option[Int]): Try[Boolean]

  def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Try[Boolean]

  def deleteGame(id: Int): Try[Boolean]

  def deletePlayer(id: Int): Try[Boolean]
}