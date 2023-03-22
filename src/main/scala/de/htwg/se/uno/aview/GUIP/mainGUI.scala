package de.htwg.se.uno
package aview.GUIP

import controller.controllerComponent.controllerInterface
import controller.controllerComponent.controllerBaseImpl.winState
import de.htwg.se.uno.util.Observer
import scala.swing.{
  BoxPanel,
  Orientation,
  GridPanel,
  Label,
  MainFrame,
  BorderPanel,
  Frame,
  FlowPanel
}
import java.awt.{Image, Toolkit, Color, Dimension}
import javax.swing.BorderFactory
import java.awt.FlowLayout

class mainGUI(controller: controllerInterface) extends MainFrame with Observer {
  title = "BEST UNO EUW"
  controller.add(this)
  iconImage = Toolkit.getDefaultToolkit.getImage(
    "src/main/resources/cards/uno_back.png"
  )
  var dpCont = displayCards(controller)
  val butts = buttonsPanel(controller)
  val menu = menubar(controller)
  val preShow = createGame(controller).ret
  var cardsPlayer = dpCont.createBoxLayout
  var cardMid = dpCont.getCardImageMid
  val cardStack = dpCont.getStackImage
  var textOut = dpCont.getText

  contents = preShow

  override def update: Unit =
    dpCont = displayCards(controller)

    if (controller.game.currentstate == winState) {
      winPop(controller).ret.open()
    }

    show.contents -= textOut
    show.contents -= midCardandStack
    midCardandStack.contents -= cardMid
    show.contents -= cardsPlayer

    cardMid = dpCont.getCardImageMid
    cardsPlayer = dpCont.createBoxLayout
    textOut = dpCont.getText

    midCardandStack.contents += cardMid
    show.contents += midCardandStack
    show.contents += textOut
    show.contents += cardsPlayer

    contents = show

  val midCardandStack = new FlowPanel() {
    contents += cardStack
    contents += cardMid
  }

  val upperLine = new BoxPanel(Orientation.Horizontal) {
    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
    contents += midCardandStack
    contents += butts.ret
  }

  val show = new BoxPanel(Orientation.Vertical) {
    contents += menu.menubar
    contents += upperLine
    contents += textOut
    contents += cardsPlayer
    minimumSize = new Dimension(550, 500)
    maximumSize = new Dimension(550, 500)
    preferredSize = new Dimension(550, 500)
    resizable = false
  }
  pack()
  centerOnScreen()
  open()
}
