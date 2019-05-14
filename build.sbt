name := "stock"
organization in ThisBuild := "com.pako"
scalaVersion in ThisBuild := "2.12.3"

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    common,
    intrinio
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )
  .disablePlugins(AssemblyPlugin)

lazy val intrinio = project
  .settings(
    name := "intrinio",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.akkaHttp,
      dependencies.sprayJson,
      dependencies.jsonStreaming
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val akkaV           = "2.5.22"
    val scalatestV      = "3.0.4"
    val akkaHttpV       = "10.1.8"
    val jsonStreamV     = "1.0.0"

    val akka           = "com.typesafe.akka"          %% "akka-stream"             % akkaV
    val akkaTest       = "com.typesafe.akka"          %% "akka-testkit"            % akkaV
    val akkaHttp       = "com.typesafe.akka"          %% "akka-http"               % akkaHttpV
    val scalatest      = "org.scalatest"              %% "scalatest"               % scalatestV
    val sprayJson      = "com.typesafe.akka"          %% "akka-http-spray-json"    % akkaHttpV
    val jsonStreaming  = "com.lightbend.akka"         %% "akka-stream-alpakka-json-streaming" % jsonStreamV
  }

lazy val commonDependencies = Seq(
  dependencies.akka,
  dependencies.akkaTest,
  dependencies.scalatest  % "test"
)

// SETTINGS

lazy val settings =
commonSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)



lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case "application.conf"            => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)
