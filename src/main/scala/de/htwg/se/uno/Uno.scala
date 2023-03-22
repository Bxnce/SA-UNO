package de.htwg.se.uno

import aview._
import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.Player
import model.gameComponent.gameBaseImpl.Card
import controller.controllerComponent.controllerInterface
import controller.controllerComponent.controllerBaseImpl._
import scala.io.StdIn.readLine //import controller._
import Console.{BLUE, RESET}
import com.google.inject.Guice
import aview.GUIP._

@main def Main: Unit =
  val injector = Guice.createInjector(new UnoModule)
  val controller = injector.getInstance(classOf[controllerInterface])
  controller.game = controller.game.init()
  println("\n" * 50)
  val tui = TUI(controller)
  val gui = mainGUI(controller)
  var input: String = ""
  while input != "q" && input != "exit" do
    Console.print(s"${BLUE}>>>  ${RESET}")
    input = readLine()
    tui.run(input)

class Kek{
  def controller_return =
    val injector = Guice.createInjector(new UnoModule)
    val controller = injector.getInstance(classOf[controllerInterface])
    controller.game = controller.game.init()
    controller
}