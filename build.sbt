import dependencies.*

val scala3Version = "3.1.2"

lazy val allDependencies = Seq(
  guice,
  scalaxml,
  playjson,
  scalactic,
  scalatest,
  scalaswing,
  scalaguice,
  akkaHttp,
  akkaHttpSpray,
  akkaHttpCore,
  akkaActorTyped,
  akkaStream,
  akkaActor,
  slf4jNop
)

lazy val persistence = (project in file("persistence"))
  .dependsOn(model)
  .settings(
    name:="UNO-Persistence",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val core = (project in file("core"))
  .dependsOn(model, persistence)
  .settings(
    name:="UNO-Core",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val model = (project in file("model"))
  .settings(
    name:="UNO-Model",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val ui = (project in file("ui"))
  .dependsOn(core)
  .settings(
    name:="UNO-Ui",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val root = (project in file("."))
  .dependsOn(core, model, ui)
  .aggregate(core, model, ui)
  .settings(
    name:="UNO",
    version:="0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val settings: Seq[Def.Setting[?]] = Seq(
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
      "*.Uno.scala"
    ),
    jacocoCoverallsServiceName := "github-actions",
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
  )

