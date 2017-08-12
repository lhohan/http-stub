name := "http-stub"
organization := "eu.lhoest"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-Xfatal-warnings", // fail compilation in case of warnings
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ypartial-unification",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions"
)


libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.9"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"

// todos plugin setup
import TodoListPlugin._
compileWithTodolistSettings
testWithTodolistSettings

// wartremover plugin setup
//wartremoverErrors ++= Warts.unsafe
// Enable all warts (some generate false positives)
wartremoverErrors ++= Warts.allBut(Wart.Any)
// Generate warnings instead of errors:
//wartremoverWarnings ++= Warts.all

// hairyfotr linter setup
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.17")
