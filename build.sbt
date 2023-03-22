val scala3Version = "3.0.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "uno",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    //crossScalaVersions ++= Seq("2.13.5", "3.0.2"),
    libraryDependencies += "com.google.inject" % "guice" % "4.2.3",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1",
    //libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
    libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.9.3")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("net.codingwell" %% "scala-guice" % "5.0.2")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test"
    ), 
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
    //jacocoCoverallsRepoToken := sys.env.get("CODECOV_REPO_TOKEN")
  )
  .enablePlugins(JacocoCoverallsPlugin)
//falls es zu Problemem mit Parallelität kommt
parallelExecution in Test := false
assemblyMergeStrategy in assembly := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case _                        => MergeStrategy.first
}
