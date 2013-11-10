name := "shmuphiscores"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.google.guava" % "guava" % "15.0"
)     

play.Project.playJavaSettings
