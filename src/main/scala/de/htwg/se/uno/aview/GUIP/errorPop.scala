package de.htwg.se.uno
package aview.GUIP

import scala.swing.{Dialog, Label, Dimension}
import javax.swing.ImageIcon

case class errorPop(text: String, cardErr: ImageIcon) {

  def ret: Dialog = new Dialog() {
    centerOnScreen

    title = "ERROR"
    contents = new Label(text) {
      icon = cardErr
    }
    modal = true
    maximumSize = new Dimension(150, 75)
    minimumSize = new Dimension(150, 75)
    preferredSize = new Dimension(150, 75)
  }
}
