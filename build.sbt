import Path._


lazy val Http4sVersion = "0.23.6"
lazy val CirceVersion = "0.14.1"
lazy val MunitVersion = "0.7.29"
lazy val LogbackVersion = "1.2.6"
lazy val MunitCatsEffectVersion = "1.0.6"
lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.17"
lazy val akkaGrpcVersion = "2.1.2"
lazy val scala213 = "2.13.4"
ThisBuild / organization := "ucab.sqa.workit"
ThisBuild / scalaVersion := scala213
ThisBuild / fork / run := true
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
.aggregate(fs2Protobuf, akkaProtobuf, social, aggregator, reports)

lazy val fs2Protobuf = (project in file("protobuf"))
.settings(
  target := (Compile / baseDirectory).value / "target/fs2" ,
  libraryDependencies ++= Seq(
    "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,
    "io.grpc" % "grpc-services"     % scalapb.compiler.Version.grpcJavaVersion,
  )
)
.enablePlugins(Fs2Grpc)

lazy val akkaProtobuf = (project in file("protobuf"))
.settings(
  target := (Compile / baseDirectory).value / "target/akka",
)
.enablePlugins(AkkaGrpcPlugin)

lazy val social = (project in file("social")).settings(
  autoCompilerPlugins := true,
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  assembly / assemblyJarName := "workit.socialMicroservice.jar",
  name := "Work-It Social Dimension",
  Compile / resourceGenerators += Def.task {
    val rootDir = baseDirectory.value.getParentFile / "certs" 
    val catarget = (Compile / resourceManaged).value / "ca"
    val cafiles = (rootDir / "ca" ** "*.pem").get pair flat(catarget)

    IO.copy(cafiles).toSeq
  }.taskValue,
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
    "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % akkaGrpcVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.typelevel" %% "cats-core" % "2.3.0",
    "org.typelevel" %% "cats-free" % "2.3.0",
    "com.typesafe.slick" %% "slick" % "3.3.3",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
    "org.postgresql" % "postgresql" % "42.2.16",
    "com.github.jwt-scala" %% "jwt-spray-json" % "9.0.2",
    "ch.megard" %% "akka-http-cors" % "1.1.2",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
    "org.scalatest" %% "scalatest" % "3.1.4" % Test,
  ),
)
.dependsOn(akkaProtobuf)

lazy val aggregator = (project in file("aggregator")).settings(
  assembly / assemblyJarName := "workit.serviceAggregator.jar",
  autoCompilerPlugins := true,
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  Compile / resourceGenerators += Def.task {
    val rootDir = baseDirectory.value.getParentFile / "certs" 
    val aggtarget = (Compile / resourceManaged).value / "aggregator"
    val aggfiles = (rootDir / "aggregator" ** "*.pem").get pair flat(aggtarget)
    val catarget = (Compile / resourceManaged).value / "ca"
    val cafiles = (rootDir / "ca" ** "*.pem").get pair flat(catarget)

    IO.copy(aggfiles ++ cafiles).toSeq
  }.taskValue,
  name := "Work-It Service Aggregator",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.3",
    "org.typelevel" %% "cats-free" % "2.3.0",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)jjj
    "org.typelevel" %% "cats-effect-kernel" % "3.3.3",
    // standard "effect" library (Queues, Console, Random etc.)
    "com.typesafe" % "config" % "1.4.1",
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
.dependsOn(fs2Protobuf)

lazy val reports = (project in file("reports"))
  .settings(
    Compile / resourceGenerators += Def.task {
      val rootDir = baseDirectory.value.getParentFile / "certs" 
      val catarget = (Compile / resourceManaged).value / "ca"
      val cafiles = (rootDir / "ca" ** "*.pem").get pair flat(catarget)

      IO.copy(cafiles).toSeq
    }.taskValue,
    organization := "ucab.sqa.workit",
    name := "reports",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "org.typelevel"   %% "cats-free"           % "2.3.0",
      "org.tpolecat"    %% "doobie-core"         % "1.0.0-RC1",
      "org.tpolecat"    %% "doobie-postgres"     % "1.0.0-RC1",
      "org.postgresql"  % "postgresql"           % "42.2.16",
      "com.github.pureconfig" %% "pureconfig" % "0.17.1",
      "com.github.jwt-scala" %% "jwt-circe" % "9.0.3"
      // "org.mongodb"     % "mongodb-driver-core"  % "4.3.3"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(fs2Protobuf)
