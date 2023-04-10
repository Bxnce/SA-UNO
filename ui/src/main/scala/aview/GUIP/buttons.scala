package aview.GUIP

import de.htwg.se.uno.controller.controllerComponent.controllerInterface

import scala.swing.*
import java.awt.Color
import javax.swing.{BorderFactory, Icon, ImageIcon}

case class buttonsPanel(controller: controllerInterface) {

  val buttons =
    new Button("Next    ") {
      reactions += { case event.ButtonClicked(_) =>
        controller.next()
      }
    }

  def ret: BoxPanel =
    new BoxPanel(Orientation.Horizontal) {
      contents += buttons
    }

}
