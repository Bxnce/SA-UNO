package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

import model.gameComponent.gameInterface
import Player._
import toCard._
import CardLayout._
import Card._
import scala.io.StdIn
import util._
import Console.{RED, GREEN, RESET}
import scala.util.{Try, Success, Failure}

case class Game(
    pList: List[Player],
    currentstate: State,
    ERROR: Int,
    cardStack: CardStack,
    midCard: Player,
    winner: Int
) extends gameInterface:

  def this(player1: String, player2: String, startstate: State) =
    this(
      List(
        Player(player1, Vector[Card](), false),
        Player(player2, Vector[Card](), false)
      ),
      startstate,
      0,
      new CardStack(
        Card.values.map(x => (x, 2)).toMap
      ),
      Player("midcard", Vector[Card](), false),
      -1
    )

  val cardsInDeck = Card.values.size - 1
  val r = scala.util.Random

  def init(): Game =
    this
      .playerFill(7)
      .take("midcard")

  //zieht eine zufällige Karte vom Stack und gibt sie dem Spieler auf die Hand -> dekrementiert die Anzahl der Karte auf dem Stack
  def take(player: String): Game =
    val rnd = r.nextInt(cardsInDeck - 4)
    add(player, Card.values(rnd))

  //fügt eine Spezifische Karte(als Card übergeben) auf die Hand eines Spielers
  def add(player: String, card: Card): Game =
    if (card.toString == "XX") {
      take(player)
    } else if (cardStack.cards(card) == 0) {
      take(player)
    } else if (
      player
        .equalsIgnoreCase("P1") || player.equalsIgnoreCase(pList(0).name)
    ) {
      copy(
        pList.updated(0, pList(0).add(card)),
        currentstate,
        ERROR,
        cardStack.decrease(card),
        midCard
      )
    } else if (
      player
        .equalsIgnoreCase("P2") || player.equalsIgnoreCase(pList(1).name)
    ) {
      copy(
        pList.updated(1, pList(1).add(card)),
        currentstate,
        ERROR,
        cardStack.decrease(card),
        midCard
      )
    } else if (player.equals("midcard")) { //würde ich rausnehmen da wir dem Stapel nie eine Karte adden
      copy(
        pList,
        currentstate,
        ERROR,
        cardStack.decrease(card),
        midCard.add(card)
      )
    } else {
      setError(-1)
    }

  def checkPlace(ind: Int, player: Int): Boolean =
    Try {
      (midCard
        .karten(0)
        .color == pList(player).karten(ind).color) || (midCard
        .karten(0)
        .value == pList(player).karten(ind).value || pList(player)
        .karten(ind)
        .color == CardColor.Black)
    } match {
      case Success(x) => x
      case Failure(y) => false
    }

  def place(ind: Int, player: Int): Game =
    if (checkPlace(ind, player) && !pList(player).placed) {
      val tmpVal = pList(player).karten(ind).value
      val tmp: Game = copy(
        pList.updated(player, pList(player).removeInd(ind)),
        currentstate,
        0,
        cardStack.increase(
          midCard.karten(0)
        ),
        Player(
          midCard.name,
          midCard.karten.updated(0, pList(player).karten(ind)),
          false
        )
      )
      tmpVal match
        case CardValue.Take2 =>
          player match
            case 0 =>
              takeCards(tmp, 2, "P2")
            case 1 =>
              takeCards(tmp, 2, "P1")
        case CardValue.Skip =>
          player match
            case 0 =>
              tmp.copy(tmp.pList.updated(1, tmp.pList(1).setTrue()))
            case 1 =>
              tmp.copy(tmp.pList.updated(0, tmp.pList(0).setTrue()))
        case CardValue.Take4 =>
          player match
            case 0 =>
              takeCards(tmp, 4, "P2")
            case 1 =>
              takeCards(tmp, 4, "P1")
        case _ =>
          tmp
    } else {
      Console.println(
        s"${RED}!!!Karte kann nicht gelegt werden!!!${RESET}"
      )
      setError(-1)
    }

  def takeCards(g: Game, num: Int, pn: String): Game =
    if (num == 0) {
      g
    } else {
      val updatedGame = g.take(pn)
      takeCards(updatedGame, num - 1, pn)
    }

  def chooseColor(color: String): Game =
    print("Farbe:" + "'" + color + "'" + "\n")
    val tmp = this
    color match
      case "Blue" | "B" =>
        changeMid(tmp, B)
      case "Red" | "R" =>
        changeMid(tmp, R)
      case "Green" | "G" =>
        changeMid(tmp, G)
      case "Yellow" | "Y" =>
        changeMid(tmp, Y)
      case _ =>
        tmp

  override def changeMid(tmp: gameInterface, c: Card): Game =
    Game(
      tmp.pList,
      tmp.currentstate,
      tmp.ERROR,
      tmp.cardStack,
      tmp.midCard.removeInd(0).add(c),
      tmp.winner
    )

  def checkWin(player: Player): Boolean =
    if (player.karten.isEmpty) {
      return true
    }
    return false

  def setError(err: Int): Game =
    copy(
      pList,
      currentstate,
      err,
      cardStack,
      midCard
    )

  def playerFill(count: Int): Game =
    val finalGame = (1 to count).foldLeft(this)((game, _) => game.take("P1").take("P2"))
    finalGame

  def getNext(game: gameInterface, player: Int, state: State): Game =
    if (player == -1) {
      Game(game.pList, state, 0, game.cardStack, game.midCard, player)
    } else {
      Game(
        game.pList
          .updated(player, game.pList(player).setFalse()),
        state,
        0,
        game.cardStack,
        game.midCard,
        player
      )
    }

  //wird nur in den Tests benutzt
  def addTest(p: String, card: Card): Game =
    copy(
      pList,
      currentstate,
      ERROR,
      cardStack.decrease(card),
      midCard.removeInd(0).add(card)
    )
