package de.htwg.se.uno
package util

import model.gameComponent.gameInterface

trait State {
  def handle(command: Command): gameInterface
}
