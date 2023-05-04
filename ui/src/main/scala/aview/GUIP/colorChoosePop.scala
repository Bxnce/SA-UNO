package aview.GUIP

import aview.UIRequest
import scala.swing.{Dialog, Dimension, GridPanel, Label}
import javax.swing.ImageIcon
import scala.swing.event.MouseClicked

case class colorChoosePop(controller: UIRequest) {

  def getImage(color: String): ImageIcon =
    new ImageIcon("Ui/src/main/resources/cards/" + color + ".png")

  def ret: Dialog = new Dialog() {
    centerOnScreen

    title = "ColorChoose"
    contents = new GridPanel(2, 2) {
      contents += new Label {
        icon = getImage("red")
        listenTo(mouse.clicks)
        reactions += { case e: MouseClicked =>
          controller.colorChoose("Red")
          close
        }
      }
      contents += new Label {
        icon = getImage("blue")
        listenTo(mouse.clicks)
        reactions += { case e: MouseClicked =>
          controller.colorChoose("Blue")
          close
        }
      }
      contents += new Label {
        icon = getImage("yellow")
        listenTo(mouse.clicks)
        reactions += { case e: MouseClicked =>
          controller.colorChoose("Yellow")
          close
        }
      }
      contents += new Label {
        icon = getImage("green")
        listenTo(mouse.clicks)
        reactions += { case e: MouseClicked =>
          controller.colorChoose("Green")
          close
        }
      }
    }
    modal = true
    maximumSize = new Dimension(150, 75)
    minimumSize = new Dimension(150, 75)
    preferredSize = new Dimension(150, 75)
  }
}
