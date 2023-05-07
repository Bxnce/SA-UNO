package fileIOComponent.database

import model.gameComponent.gameInterface

trait DAOInterface {
  def save(game: gameInterface): Unit

  def load(id:Option[Int]): gameInterface
}
