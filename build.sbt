import dependencies.*

val scala3Version = "3.1.2"
/* =====================================================================================================================
 * General Settings
 * ===================================================================================================================== */
ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "3.1.2"
ThisBuild / organization := "org.uno"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / fork := true

/* =====================================================================================================================
 * GitHub Packages Settings
 * ===================================================================================================================== */
ThisBuild / resolvers += "GitHub SA-Uno Packages" at "https://maven.pkg.github.com/bxnce/sa-uno"
ThisBuild / publishTo := Some("GitHub SA-Uno Apache Maven Packages" at "https://maven.pkg.github.com/bxnce/sa-uno")
ThisBuild / publishConfiguration := publishConfiguration.value.withOverwrite(true)
ThisBuild / publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
ThisBuild / publishMavenStyle := true
ThisBuild / credentials ++= {
  val mvnCredentials = Path.userHome / ".sbt" / ".credentials"
  val defaultRealm = "GitHub Package Registry"
  val defaultUser = "SA-Uno"
  Seq(
    mvnCredentials match {
      case credFile if credFile.exists() => Credentials(credFile)
      case _ => Credentials(
        defaultRealm,
        "maven.pkg.github.com",
        defaultUser,
        System.getenv("GH_PACKAGE_KEY")
      )
    }
  )
}

//Docker Publish
ThisBuild / Compile / discoveredMainClasses := Seq()
ThisBuild / dockerUpdateLatest := true
ThisBuild / dockerBaseImage := "openjdk:17-jdk"
ThisBuild / Docker / dockerRepository := Some("docker.io")
ThisBuild / Docker / dockerUsername := Some("nu33")
ThisBuild / Docker / publish := {}

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
  slf4jNop,
  slick,
  postgresql
)

lazy val persistence = (project in file("persistence"))
  .dependsOn(model)
  .settings(
    name := "UNO-Persistence",
    version := "1.0",
    dockerExposedPorts := Seq(8081),
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val core = (project in file("core"))
  .dependsOn(model, persistence)
  .settings(
    name := "UNO-Core",
    version := "1.0",
    dockerExposedPorts := Seq(8080),
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val model = (project in file("model"))
  .settings(
    name := "UNO-Model",
    version := "1.0",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val gui = (project in file("gui"))
  .dependsOn(core)
  .settings(
    name := "UNO-Gui",
    version := "1.0",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val tui = (project in file("tui"))
  .dependsOn(core)
  .settings(
    name := "UNO-Tui",
    version := "1.0",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val root = (project in file("."))
  .dependsOn(core, model, gui, tui, persistence)
  .aggregate(core, model, gui, tui, persistence)
  .settings(
    name := "UNO",
    version := "1.0",
    dockerExposedPorts := Seq(8082),
    scalaVersion := scala3Version,
    publishArtifact := false,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

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

