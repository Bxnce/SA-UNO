package scala

import scala.Console.{BLUE, RESET}
import scala.io.StdIn.readLine
import controller.controllerComponent.ControllerAPI
import aview.GUIP.mainGUI
import fileIOComponent.RestAPIPersistence
import aview.TUI
import UnoModule.given_controllerInterface

@main def Main: Unit =
  ControllerAPI().start()
  RestAPIPersistence().start()

  println("\n" * 50)
  val tui = TUI()
  //mainGUI()
  var input: String = ""
  while input != "q" && input != "exit" do
    Console.print(s"${BLUE}>>>  ${RESET}")
    input = readLine()
    tui.run(input)
