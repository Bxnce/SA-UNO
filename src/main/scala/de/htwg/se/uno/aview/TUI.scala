package de.htwg.se.uno
package aview

import scala.io.StdIn.readLine
import controller.controllerComponent.controllerInterface
import model.gameComponent.gameBaseImpl.Game
import util.Observer
import Console.{RED, GREEN, RESET}
import de.htwg.se.uno.model.gameComponent.gameBaseImpl.CardValue
import de.htwg.se.uno.model.fileIOComponent.XMLImpl._

class TUI(controller: controllerInterface) extends Observer:
  val ERROR = -1
  val EXIT = 0
  val SUCCESS = 1
  controller.add(this)
  print(
    "\n\nWelcome to UNO! type help for all commands\n\n"
  )

  def run(input: String) =
    convertinputString(input) match
      case ERROR   => printhelp()
      case EXIT    => System.exit(0)
      case SUCCESS => print("\n\n")
  //case _       => print("Hier sollten sie nicht hinkommen\n")

  def convertinputString(input: String): Int =
    if (input.size == 0)
      print("no input!\n")
      return ERROR
    val in = input.split(" ")

    in(0) match
      case "s" =>
        print(controller.return_j)
        return SUCCESS
      case "l" =>
        controller.load
        return SUCCESS
      case "exit" | "q" => return EXIT

      case "help" | "h" =>
        printhelp()
        return SUCCESS

      case "new" =>
        controller.newG(readLine("Name1:    "), readLine("Name2:    "))
        return SUCCESS

      case "take" | "+" =>
        controller.take()
        if (controller.game.ERROR < 0) {
          Console.println(
            s"${RED}!!!take or + is not possible in this state!!!${RESET}"
          )
          return ERROR
        }
        return SUCCESS;

      case "place" | "-" =>
        if (in.size < 2) {
          Console.println(s"${RED}Wrong place command!${RESET}")
          return ERROR
        } else {
          controller.place(in(1).toInt - 1)
          if (controller.game.ERROR < 0) {
            Console.println(
              s"${RED}!!!place or - is not possible in this state!!!${RESET}"
            )
            controller.game = controller.game.setError(0)
            return ERROR
          } else {
            if (
              controller.game.midCard
                .karten(0)
                .getValue == CardValue.Wildcard || controller.game.midCard
                .karten(0)
                .getValue == CardValue.Take4
            ) {
              var check = true
              var color = ""
              while (check)
              {
                color = readLine(
                  "please select your color (Yellow(Y), Blue(B), Green(G) or Red(R)):  "
                )
                print(color + "\n")
                color match
                  case "Yellow" | "Y" | "Blue" | "B" | "Green" | "G" | "Red" |
                      "R" =>
                    check =
                      false //statt break und continue diese hässliche Variable.
                  case _ =>
                    print(
                      "choose one of the given colors or shortcuts for colors! \n"
                    )

              }
              controller.colorChoose(color)
            }
            return SUCCESS
          }
        }

      case "next" | "n" =>
        controller.next()
        return SUCCESS
      case "undo" | "z" =>
        controller.undo()
        return SUCCESS
      case "redo" | "y" =>
        controller.redo()
        return SUCCESS
      case _ =>
        print("Wrong input these are all valid commands:\n")
        return ERROR

  def printhelp() =
    Console.print(s"""${GREEN}
              all commands for UNO:
              - help | h                       : shows all commands
              - exit | q                       : leaves the game
              - new  |                         : creates a new game
              - take | +                       : adds a random card from the stack to the player
              - place <index> | - <index>      : places the card at <index>
              - next | n                       : goes to the next state   
              - undo | z                       : undo the last command
              - redo | y                       : redo, undos the last undo
              ${RESET}""" + "\n")

  override def update: Unit =
    print(controller.toString())
