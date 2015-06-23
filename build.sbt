name := "TwiTrav"

organization := "com.travis"

scalaVersion := "2.11.4"

scalacOptions := Seq("-feature","-unchecked","-deprecation","-encoding","utf8")

libraryDependencies ++= {
	Seq(
		"org.json4s" %% "json4s-native" % "3.2.11",
		"org.json4s" %% "json4s-jackson" % "3.2.11",
		"org.twitter4j" % "twitter4j-stream" % "4.0.3"
	)
}


