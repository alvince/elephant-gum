import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    kotlin("jvm")
}

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    mavenCentral()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}