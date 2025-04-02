plugins {
  id("java")
  id("java-library")
}

allprojects {
  apply(plugin = "java")
  apply(plugin = "java-library")

  group = "bot.inker.krypix"
  version = "1.0-SNAPSHOT"

  repositories {
    mavenCentral()
    maven("https://r.irepo.space/maven")
  }
}

dependencies {
  api(libs.jetbrains.annotations)

  api(libs.asm.core)
  api(libs.asm.commons)
  api(libs.asm.util)
  api(libs.asm.tree)
  api(libs.asm.analysis)

  api(libs.commons.lang3)
  api(libs.commons.io)
  api(libs.guava)

  api(libs.rocksdbjni)
  api(libs.fastutil)

  api(libs.jgrapht.core)
  api(libs.jgrapht.ext)
  api(libs.jgrapht.io)

  api(libs.jline.terminal.core)
  api(libs.jline.terminal.jni)
  api(libs.jline.reader)

  api(libs.slf4j)
  api(libs.log4j.slf4j2)
  api(libs.log4j.jul)
  api(libs.log4j.iostreams)
}
