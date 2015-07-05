name := "TwiTrav"

organization := "com.travis"

scalaVersion := "2.11.4"

scalacOptions := Seq("-feature","-unchecked","-deprecation","-encoding","utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "org.json4s"          %% "json4s-native"    % "3.2.11",
    "org.json4s"          %% "json4s-jackson"   % "3.2.11",
    "org.twitter4j"       %  "twitter4j-stream" % "4.0.3",
    "com.typesafe.akka"   %% "akka-actor"       % "2.3.11",
    "io.spray"            %%  "spray-can"       % sprayV,
    "io.spray"            %%  "spray-routing"   % sprayV,
    "io.spray"            %%  "spray-testkit"   % sprayV   % "test",
    "com.typesafe.akka"   %%  "akka-actor"      % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"    % akkaV    % "test",
    "org.specs2"          %%  "specs2-core"     % "2.3.11" % "test"
  )
}

Revolver.settings
