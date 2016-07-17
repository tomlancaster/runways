name := """lunatech-reporter"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.3"

libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"

libraryDependencies += "org.webjars" % "jquery" % "2.2.4"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers ++= Seq(
  "webjars"    at "http://webjars.github.com/m2"
)
