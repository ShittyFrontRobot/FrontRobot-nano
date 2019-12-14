import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "org.mechdancer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.java.dev.jna", "jna-platform", "5.5.0")
    implementation("com.fazecast", "jSerialComm", "2.5.3")
    implementation ("org.mechdancer","linearalgebra","0.2.6-dev-2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks

compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}