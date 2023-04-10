import sbt._
import Keys._


object dependencies {
  val guice = "com.google.inject" % "guice" % "4.2.3"
  val scalaxml = "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
  val playjson = ("com.typesafe.play" %% "play-json" % "2.9.3").cross(CrossVersion.for3Use2_13)
  val scalactic = "org.scalactic" %% "scalactic" % "3.2.10"
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.10" % "test"
  val scalaswing = ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13)
  val scalaguice = ("net.codingwell" %% "scala-guice" % "5.0.2").cross(CrossVersion.for3Use2_13)
}