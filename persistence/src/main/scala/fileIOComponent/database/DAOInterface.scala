package fileIOComponent.database

import model.gameComponent.gameInterface

import scala.concurrent.Future
import scala.util.Try

trait DAOInterface {
  def save(game: gameInterface): Future[Unit]

  def load(id: Option[Int]): Future[Try[gameInterface]]
  
  def updateGame(id: Int, player1: Option[Int], player2: Option[Int], midCard: Option[Int], currentstate: Option[String], error: Option[Int], cardstack: Option[String], winner: Option[Int]): Future[Try[Boolean]]

  def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Future[Try[Boolean]]

  def deleteGame(id: Int): Future[Try[Boolean]]

  def deletePlayer(id: Int): Future[Try[Boolean]]
}