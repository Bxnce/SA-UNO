package fileIOComponent.database.sqlTables

import spray.json._
import slick.jdbc.MySQLProfile.api.*


class PlayerTable(tag: Tag) extends Table[(Int, String, String, Int, Boolean)](tag, "PLAYER") :
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def cards = column[String]("CARDS")

  def card_count = column[Int]("CARD_COUNT")

  def placed = column[Boolean]("PLACED")


  override def * = (id, name, cards, card_count, placed)