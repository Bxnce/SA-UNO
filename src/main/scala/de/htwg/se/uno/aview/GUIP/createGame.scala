package de.htwg.se.uno
package aview.GUIP

import controller.controllerComponent.controllerInterface
import scala.swing._
import java.awt.Color
import javax.swing.BorderFactory

case class createGame(controller: controllerInterface) {

  val name1 = new TextArea("Name 1") {
    background = Color.GRAY.brighter
    foreground = Color.BLACK
    font = new Font("Arial", 2, 40)
    border = BorderFactory.createLineBorder(Color.BLACK, 2)
    minimumSize = new Dimension(100, 40)
    maximumSize = new Dimension(100, 40)
    preferredSize = new Dimension(100, 40)
  }

  val name2 = new TextArea("Name 2") {
    background = Color.GRAY.brighter
    foreground = Color.BLACK
    font = new Font("Arial", 2, 40)
    border = BorderFactory.createLineBorder(Color.BLACK, 2)
    minimumSize = new Dimension(100, 40)
    maximumSize = new Dimension(100, 40)
    preferredSize = new Dimension(100, 40)
  }

  val button = new Button("create game") {
    reactions += { case event.ButtonClicked(_) =>
      controller.newG(name1.text, name2.text)
    }
    border = BorderFactory.createRaisedSoftBevelBorder
    font = new Font("Arial", 1, 40)
    minimumSize = new Dimension(100, 40)
    maximumSize = new Dimension(100, 40)
    preferredSize = new Dimension(100, 40)
  }

  def ret: GridPanel =
    new GridPanel(3, 1) {
      contents += name1
      contents += name2
      contents += button
      minimumSize = new Dimension(300, 300)
      maximumSize = new Dimension(300, 300)
      preferredSize = new Dimension(300, 300)
    }
}
