package de.htwg.se.uno
package model.fileIOComponent

import model.gameComponent.gameInterface

trait FileIOInterface {
  def load: gameInterface
  def save(game: gameInterface): Unit

  def return_json(game: gameInterface): String
}

object FileIOInterface {
  def apply(): FileIOInterface =
    new JSONImpl.fileIO()
}
