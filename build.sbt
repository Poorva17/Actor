name := "Scala"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed"  % "2.6.10",
  "com.typesafe.akka" %% "akka-remote"       % "2.6.10",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
