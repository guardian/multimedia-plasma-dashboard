logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.9")

addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")