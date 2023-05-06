package gui.GUIP

import scala.swing.*
import java.awt.Color
import javax.swing.{BorderFactory, Icon, ImageIcon}
import gui.UIRequest

case class buttonsPanel(controller: UIRequest) {

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
