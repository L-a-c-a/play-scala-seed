name := """play-scala-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

//innen én
libraryDependencies += "ru.yandex.qatools.ashot" % "ashot" % "latest.integration"
libraryDependencies += "org.seleniumhq.selenium" % "htmlunit-driver" % "latest.integration"
// org.openqa.selenium.internal.Base64Encoder miatt (ScreenCaptureHtmlUnitDriver miatt):
libraryDependencies += "org.seleniumhq.selenium" % "selenium-common" % "latest.integration"
