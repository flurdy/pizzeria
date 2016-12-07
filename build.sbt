name := """pizzeria"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "com.h2database"    %  "h2"        % "1.4.192",
  "com.typesafe.play" %% "anorm"     % "2.5.0"
)

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)
