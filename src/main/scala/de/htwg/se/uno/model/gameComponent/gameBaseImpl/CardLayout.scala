package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

object CardLayout {

  val eol = "\n"
  val row = "+" + "-" * 2
  val rowEnd = "+" + eol

  def udRow(cCount: Int): String =
    row * cCount + rowEnd
}
