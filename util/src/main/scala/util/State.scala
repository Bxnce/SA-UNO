package util

import de.htwg.se.uno.model.gameComponent.gameInterface

trait State {
  def handle(command: Command): gameInterface
}
