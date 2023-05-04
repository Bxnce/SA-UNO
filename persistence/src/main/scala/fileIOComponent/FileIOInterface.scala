package fileIOComponent

import model.gameComponent.gameInterface

trait FileIOInterface {
  def load: gameInterface
  def save(game: gameInterface): Unit

  def jsonToGame(json_str: String): gameInterface
}
