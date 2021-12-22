lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.17"

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "ucab.sqa.workit",
      scalaVersion := "2.13.4"
    )
  ),
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
    "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
    "com.github.jwt-scala" %% "jwt-circe" % "9.0.2"
  )
)
