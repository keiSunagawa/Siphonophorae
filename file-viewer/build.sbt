import Dependencies._

scalaJSModuleKind := ModuleKind.CommonJSModule
enablePlugins(ScalaJSPlugin)

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.kerfume"

lazy val root = (project in file("."))
  .settings(
    name := "file-viewer",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.1",
      "org.typelevel" %%% "cats-core" % "1.6.0",
      "org.typelevel" %%% "cats-free" % "1.6.0"
    ),
    scalacOptions ++= Seq(
      "-P:scalajs:sjsDefinedByDefault"
    )
  )
