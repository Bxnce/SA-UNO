package scala

import com.google.inject.Guice
import scala.Console.{BLUE, RESET}
import scala.io.StdIn.readLine
import controller.controllerComponent.controllerInterface
import scala._
import aview.GUIP.mainGUI
import aview.TUI

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
