package util

import model.gameComponent.gameInterface

import scala.util.Command

trait State {
  def handle(command: Command): gameInterface
}
