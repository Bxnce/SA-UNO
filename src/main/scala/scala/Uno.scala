package scala

import com.google.inject.Guice
import scala.Console.{BLUE, RESET}
import scala.io.StdIn.readLine
import controller.controllerComponent.controllerInterface
import controller.controllerComponent.RestAPI
import scala._
import aview.GUIP.mainGUI
import aview.TUI
import UnoModule.given_controllerInterface

@main def Main: Unit =
  val controllerApi = RestAPI()
  controllerApi.start()
  controller.game = controller.game.init()
  println("\n" * 50)
  val tui = TUI(controller)
  val gui = mainGUI(controller)
  var input: String = ""
  while input != "q" && input != "exit" do
    Console.print(s"${BLUE}>>>  ${RESET}")
    input = readLine()
    tui.run(input)
