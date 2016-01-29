name := "Abiturient"

version := "1.0"

lazy val `abiturient` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc41",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.12",
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.akka" %% "akka-remote" % "2.3.12",
  "com.typesafe.play" %% "play-json" % "2.4.2",
  // Logging
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.12",
  filters,
  "com.typesafe.play" %% "play-ws" % "2.4.2",
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "com.artofsolving" % "jodconverter" % "2.2.1",
  specs2 % Test,
  // WebJars pull in client-side web libraries(will be used :))
  "org.webjars" % "webjars-play_2.11" % "2.4.0-1",
  "org.webjars" % "jquery" % "1.11.3",
  "org.webjars" % "jquery-ui" % "1.11.4",
  "org.webjars" % "knockout" % "3.3.0",
  "org.webjars" % "requirejs" % "2.1.20",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "bootstrap-select" % "1.7.3-1",
  "org.webjars" % "lodash" % "3.10.1",
  "org.webjars" % "respond" % "1.4.2",
  "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % "4.15.35",
  "org.webjars" % "momentjs" % "2.10.6"
)

routesGenerator := InjectedRoutesGenerator
