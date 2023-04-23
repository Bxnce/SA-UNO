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

  val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.5.0"
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0"
  val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % "10.5.0"
  val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0"
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.8.0"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.8.0"

  val slf4jNop = "org.slf4j" % "slf4j-nop" % "2.0.5"
}