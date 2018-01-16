name := "mmplasmadash"
 
version := "1.0" 
      
lazy val `mmplasmadash` = (project in file(".")).enablePlugins(PlayScala, RiffRaffArtifact, SystemdPlugin)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

//AWS
libraryDependencies ++= Seq(
  "com.gu" %% "scanamo" % "1.0.0-M2" exclude("commons-logging","commons-logging")
)


//logging
libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  // https://mvnrepository.com/artifact/ch.qos.logback/logback-core
  "ch.qos.logback" % "logback-core" % "1.2.3",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.25"
)

//circe
val circeVersion = "0.9.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

//panda
libraryDependencies +="com.gu" %% "hmac-headers" % "1.0"
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