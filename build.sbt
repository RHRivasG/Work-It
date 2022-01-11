lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.17"
lazy val scala213 = "2.13.4"
ThisBuild / organization := "ucab.sqa.workit"
ThisBuild / scalaVersion := scala213
ThisBuild / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.last.endsWith(".properties") || ps.last.endsWith(".proto") => MergeStrategy.last
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
.settings(
  publish / skip := true,
)
.aggregate(protobuf, social, aggregator)

lazy val protobuf = (project in file("protobuf"))
.settings(
  libraryDependencies ++= Seq(
    "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,
    "io.grpc" % "grpc-services"     % scalapb.compiler.Version.grpcJavaVersion,
  )
)
.enablePlugins(Fs2Grpc)

lazy val social = (project in file("social")).settings(
  autoCompilerPlugins := true,
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  assembly / assemblyJarName := "workit.socialMicroservice.jar",
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
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
    "org.postgresql" % "postgresql" % "42.2.16",
    "com.github.jwt-scala" %% "jwt-spray-json" % "9.0.2",
    "ch.megard" %% "akka-http-cors" % "1.1.2"
  ),
  dockerAutoPackageJavaApplication()
)
.enablePlugins(DockerPlugin)
.dependsOn(protobuf)

lazy val aggregator = (project in file("aggregator")).settings(
  assembly / assemblyJarName := "workit.serviceAggregator.jar",
  autoCompilerPlugins := true,
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  name := "Work-It Service Aggregator",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.3",
    "org.typelevel" %% "cats-free" % "2.3.0",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)jjj
    "org.typelevel" %% "cats-effect-kernel" % "3.3.3",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.3",
    "co.fs2" %% "fs2-core" % "3.2.0",
    "co.fs2" %% "fs2-io" % "3.2.0",
    "io.github.kirill5k" %% "mongo4cats-core" % "0.4.2",
    "io.github.kirill5k" %% "mongo4cats-circe" % "0.4.2",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-simple" % "1.7.5",
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.4.0" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
  )
)
.dependsOn(protobuf)