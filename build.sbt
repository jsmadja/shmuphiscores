name := """shmuphiscores"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.17",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.ocpsoft.prettytime" % "prettytime" % "3.1.0.Final",
  "rome" % "rome" % "1.0",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.13",
  "org.easytesting" % "fest-assert" % "1.4",
  "junit" % "junit" % "4.11",
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator
