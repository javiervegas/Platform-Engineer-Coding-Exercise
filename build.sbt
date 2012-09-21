import AssemblyKeys._ // put this at the top of the file

assemblySettings

jarName in assembly := "TflyServer.jar"

test in assembly := {}

name := "TicketFly Server"

version := "1.0"

scalaVersion := "2.9.2"

libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test->default"

libraryDependencies ++= Seq("org.specs2" %% "specs2" % "1.12.1" % "test")

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.9.2")

scalacOptions += "-P:continuations:enable"
