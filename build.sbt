name := "sappho"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test

ThisBuild / useCoursier := false