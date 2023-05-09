package gui.GUIP

import gui.UIRequest

import scala.swing.{BoxPanel, Button, Dialog, Dimension, Font, Label, Orientation, event}
import java.awt.Color
import javax.swing.ImageIcon
import scala.swing.event.MouseClicked
import javax.swing.BorderFactory

case class winPop(controller: UIRequest) {

  def ret: Dialog = new Dialog() {
    modal = true
    centerOnScreen
    title = "winner: " + controller.game.pList(controller.game.winner).name
    contents = new BoxPanel(Orientation.Vertical) {
      minimumSize = new Dimension(362, 420)
      maximumSize = new Dimension(362, 420)
      preferredSize = new Dimension(362, 420)
      contents += new Label() {
        icon = new ImageIcon("Ui/src/main/resources/cards/anime-naruto.gif")
      }
      contents += new Label(
        controller.game.pList(controller.game.winner).name + " won"
      ) {
        font = new Font("Arial", 3, 60)
        border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
      }
      contents += new Label("restart game") {
        listenTo(mouse.clicks)
        font = new Font("Arial", 3, 60)
        foreground = Color.RED
        border = BorderFactory.createRaisedBevelBorder
        reactions += { case e: MouseClicked =>
          controller.next()
          dispose()
        }
      }
    }
    resizable = false
  }
}
