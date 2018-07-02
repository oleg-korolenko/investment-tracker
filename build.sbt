lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.8"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.investment",
      scalaVersion    := "2.12.4"
    )),
    name := "investment-api",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",   // source files are in UTF-8
      "-deprecation",         // warn about use of deprecated APIs
      "-unchecked",           // warn about unchecked type parameters
      "-feature",             // warn about misused language features
      "-language:higherKinds",// allow higher kinded types without `import scala.language.higherKinds`
      "-Xlint",               // enable handy linter warnings
      "-Xfatal-warnings",     // turn compiler warnings into errors
      "-Ypartial-unification" // allow the compiler to unify type constructors of different arities
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka" % "0.19",

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )
  )
