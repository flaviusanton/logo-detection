libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.2"

libraryDependencies += "commons-codec" % "commons-codec" % "1.9"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.1"
// lib org.json4s might be better if messages get more complex
//libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.8.1.1" exclude("com.sun.jmx", "jmxri") exclude("com.sun.jdmk", "jmxtools")
