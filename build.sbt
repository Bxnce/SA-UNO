import sbt.Keys.libraryDependencies
import dependencies._

val scala3Version = "3.1.2"



lazy val allDependencies = Seq(
  guice,
  scalaxml,
  playjson,
  scalactic,
  scalatest,
  scalaswing,
  scalaguice
)

lazy val util: Project = Project(id = "UNO-Util", base = file("Util"))
  .dependsOn(model)
  .settings(
    name:="UNO-Util",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val core: Project = Project(id = "UNO-Core", base = file("Core"))
  .dependsOn(model, util)
  .settings(
    name:="UNO-Core",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val model: Project = Project(id = "UNO-Model", base = file("Model"))
  .settings(
    name:="UNO-Model",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val ui: Project = Project(id = "UNO-Ui", base = file("Ui"))
  .dependsOn(core)
  .settings(
    name:="UNO-Ui",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val root: Project = Project(id = "UNO", base = file("."))
  .dependsOn(util, core, model, ui)
  .settings(
    name:="UNO",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val settings: Seq[Def.Setting[_]] = Seq(
    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(
        JacocoReportFormats.ScalaHTML,
        JacocoReportFormats.XML
      ), // note XML formatter
      "utf-8"
    ),
    jacocoExcludes := Seq(
      "*aview.*",
      "*fileIOComponent.*",
      "*.UnoModule.scala",
      "*.Uno.scala"
    ),
    jacocoCoverallsServiceName := "github-actions",
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
  )

