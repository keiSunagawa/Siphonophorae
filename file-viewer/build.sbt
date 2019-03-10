import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.kerfume"

lazy val frontendBackend = (project in file("frontend-backend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "frontend-backend",
    libraryDependencies += scalaTest % Test,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.1",
      "org.typelevel" %%% "cats-core" % "1.6.0",
      "org.scalaz" %% "scalaz-zio" % "1.0-RC1",
      "io.frees" %% "frees-core"  % "0.8.2"
    ),
    scalacOptions ++= Seq(
      "-P:scalajs:sjsDefinedByDefault"
    )
  )
