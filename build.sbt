lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.17"
lazy val scala213 = "2.13.4"
lazy val scala310 = "3.1.0"
lazy val supportedScalaVersions = Seq(scala213, scala310)
ThisBuild / organization := "ucab.sqa.workit"
ThisBuild / scalaVersion := scala310

lazy val root = (project in file("."))
  .settings(
    crossScalaVersions := Nil,
    publish / skip := true,
  )
  .aggregate(protobuf.projectRefs ++ social.projectRefs ++ aggregator.projectRefs :_*)

lazy val protobuf = (projectMatrix in file("protobuf"))
.settings(
  libraryDependencies ++= Seq(
    "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,
  )
)
.enablePlugins(Fs2Grpc)
.jvmPlatform(scalaVersions = supportedScalaVersions)

lazy val social = (projectMatrix in file("social")).settings(
  autoCompilerPlugins := true,
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  name := "Work-It Social Dimension",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.1.4" % Test,
    "org.typelevel" %% "cats-core" % "2.3.0",
    "org.typelevel" %% "cats-free" % "2.3.0",
    "com.typesafe.slick" %% "slick" % "3.3.3",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
    "org.postgresql" % "postgresql" % "42.2.16",
    "com.github.jwt-scala" %% "jwt-spray-json" % "9.0.2",
    "ch.megard" %% "akka-http-cors" % "1.1.2"
  )
)
.dependsOn(protobuf)
.jvmPlatform(scalaVersions = Seq(scala213))

lazy val aggregator = (projectMatrix in file("aggregator")).settings(
  name := "Work-It Service Aggregator",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.3",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.3",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.3",
    "co.fs2" %% "fs2-core" % "3.2.0",
    "co.fs2" %% "fs2-io" % "3.2.0",
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.4.0" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
  )
)
.dependsOn(protobuf)
.jvmPlatform(scalaVersions = Seq(scala310))