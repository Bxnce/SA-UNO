package de.htwg.se.uno
package aview.GUIP

import controller.controllerComponent.controllerInterface
import scala.swing._

case class menubar(controller: controllerInterface) {
  val menubar = new MenuBar {
    val dim = new Dimension(550, 20)
    maximumSize = dim
    minimumSize = dim
    preferredSize = dim
    contents ++= Seq(
      new Menu("File") {
        contents ++= Seq(
          MenuItem(Action("Load")(controller.load)),
          MenuItem(Action("Save")(controller.save)),
          MenuItem(Action("Undo")(controller.undo())),
          MenuItem(Action("Redo")(controller.redo()))
        )
      }
    )
  }
}
