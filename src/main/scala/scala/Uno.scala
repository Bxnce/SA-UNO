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

  println("\n" * 50)
  val tui = TUI()
  val gui = mainGUI()
  var input: String = ""
  while input != "q" && input != "exit" do
    Console.print(s"${BLUE}>>>  ${RESET}")
    input = readLine()
    tui.run(input)
