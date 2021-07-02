organization := "fi.zapzap.sentiment"
name := "sentiment-backend"
version := "1.7"

scalaVersion := "2.13.4"
// For Settings/Task reference, see http://www.scala-sbt.org/release/sxr/sbt/Keys.scala.html

lazy val scalaTestVersion = "3.2.0-M2"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "1.0.8",
  //"dev.zio" %% "zio-streams" % "1.0.8",
  "dev.zio" %% "zio-config" % "1.0.6",
  "dev.zio" %% "zio-cache" % "0.1.0",
  "dev.zio" %% "zio-json" % "0.1.5",
  "dev.zio" %% "zio-logging" % "0.5.8",

  "io.d11" %% "zhttp" % "1.0.0.0-RC17",

  "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
  "org.postgresql" % "postgresql" % "42.2.20",

  "org.scalatest" %% "scalatest-freespec" % scalaTestVersion % "test",
  "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.2" % "test"
)

testOptions in Test += Tests.Argument(
  TestFrameworks.ScalaCheck, "-maxSize", "5", "-minSuccessfulTests", "33",
  "-workers", s"${java.lang.Runtime.getRuntime.availableProcessors - 1}" ,
  "-verbosity", "1"
)