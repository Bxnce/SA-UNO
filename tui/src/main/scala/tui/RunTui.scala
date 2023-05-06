package tui

import scala.Console.{BLUE, RESET}
import scala.io.StdIn.readLine
import controller.controllerComponent.ControllerAPI
import fileIOComponent.RestAPIPersistence

object RunTui {
  @main def run =
    println("\n" * 50)
    val tui = TUI()
    var input: String = ""
    while input != "q" && input != "exit" do
      Console.print(s"${BLUE}>>>  ${RESET}")
      input = readLine()
      tui.run(input)
}