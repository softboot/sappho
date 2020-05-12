name := "sappho"

version := "0.1"

scalaVersion := "2.13.1"

scalacOptions += "-deprecation"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test

ThisBuild / useCoursier := false