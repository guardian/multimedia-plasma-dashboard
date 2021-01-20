name := "mmplasmadash"
 
version := "1.0" 
      
lazy val `mmplasmadash` = (project in file(".")).enablePlugins(PlayScala, RiffRaffArtifact, SystemdPlugin)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.13"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )


//AWS
libraryDependencies ++= Seq(
  "org.scanamo" %% "scanamo" % "1.0-M13" exclude("commons-logging","commons-logging"),
  "org.scanamo" %% "scanamo-formats" % "1.0.0-M11",
  "software.amazon.awssdk" % "dynamodb" % "2.15.66",
  "software.amazon.awssdk" % "auth" % "2.15.66",
)

//logging
libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  // https://mvnrepository.com/artifact/ch.qos.logback/logback-core
  "ch.qos.logback" % "logback-core" % "1.2.3",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.30"
)

//circe
val circeVersion = "0.13.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

/*
vulnerable dependencies identified by Snyk
 */
libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.5.13",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.5.1"
)
// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "30.1-jre"


debianPackageDependencies := Seq("openjdk-8-jre-headless")
serverLoading in Debian := Some(ServerLoader.Systemd)
serviceAutostart in Debian := false

version in Debian := s"${version.value}-${sys.env.getOrElse("CIRCLE_BUILD_NUM","SNAPSHOT")}"
name in Debian := "plasmadash"

maintainer := "Andy Gallagher <andy.gallagher@theguardian.com>"
packageSummary := "A dashboard to search for media atoms not attached to asset management"
packageDescription := """A dashboard to search for media atoms not attached to asset management"""
riffRaffPackageType := (packageBin in Debian).value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffManifestBranch := sys.env.getOrElse("CIRCLE_BRANCH","unknown")
riffRaffManifestRevision := sys.env.getOrElse("CIRCLE_BUILD_NUM","SNAPSHOT")
riffRaffManifestVcsUrl := sys.env.getOrElse("CIRCLE_BUILD_URL", "")
riffRaffBuildIdentifier := sys.env.getOrElse("CIRCLE_BUILD_NUM", "SNAPSHOT")
riffRaffPackageName := "plasmadash"
riffRaffManifestProjectName := "multimedia:plasmadash"