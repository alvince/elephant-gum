import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.1"
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
    testImplementation("junit:junit:4.12")
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

pluginBundle {
    website = property("project.website").toString()
    vcsUrl = property("project.website").toString()
    tags = listOf("properties", "gradle")
}

gradlePlugin {
    plugins {
        create("propertiesLoaderPlugin") {
            id = property("plugin.id").toString()
            displayName = property("plugin.displayName").toString()
            description = property("plugin.description").toString()
            version = property("plugin.version").toString()
            implementationClass = "cn.alvince.gradle.gum.properties.PropsLoaderPlugin"
        }
    }
}
