name := "shmuphiscores"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.google.guava" % "guava" % "15.0",
  "mysql" % "mysql-connector-java" % "5.1.17",
  "org.ocpsoft.prettytime" % "prettytime" % "3.1.0.Final",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.13",
  "rome" % "rome" % "1.0"
)

play.Project.playJavaSettings
