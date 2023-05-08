package fileIOComponent.database.sqlTables

import slick.jdbc.PostgresProfile.api.*
import slick.lifted.ForeignKeyQuery
import spray.json.*

import scala.annotation.targetName

class GameTable(tag: Tag) extends Table[(Int, Int, Int, Int, String, Int, String, Int)](tag, "GAME") :
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def player1Id = column[Int]("PLAYER1")

  def player2Id = column[Int]("PLAYER2")

  def midcardId = column[Int]("MIDCARD")

  def currentstate = column[String]("CURRENTSTATE")

  def error = column[Int]("ERROR")

  def cardstack = column[String]("CARDSTACK")

  def winner = column[Int]("WINNER")

  def * = (id, player1Id, player2Id, midcardId, currentstate, error, cardstack, winner)

  //foreign keys doesnt work. why no one knows scala is just a big dump of shit
  //def player1 = foreignKey("PLAYER1_FK", player1Id, player)(targetColumns = _.id)

  //def player2 = foreignKey("PLAYER2_FK", player2Id, player)(targetColumns = _.id)

  //def midcard = foreignKey("MIDCARD_FK", midcardId, player)(targetColumns = _.id)

  //val player = new TableQuery(PlayerTable(_))

