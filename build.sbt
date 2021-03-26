ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "co.reality"
ThisBuild / organizationName := "Reality Games"

name := "reality-venue"

ThisBuild / scalacOptions := Seq("-unchecked",
                                 "-deprecation",
                                 "-encoding",
                                 "utf8",
                                 "-feature",
                                 "literal-types",
                                 "-Xfatal-warnings",
                                 "-Ymacro-annotations")

lazy val `reality-venue` = (project in file("."))
  .settings(publish := {})
  .aggregate(`domain`)

lazy val `domain` = (project in file("domain"))

lazy val `api` = (project in file("api")).dependsOn(`domain` % "compile->compile;test->test")