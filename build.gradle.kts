plugins {
    `java-library`
    kotlin("jvm") version "2.1.20"
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "dev.lenvx"
version = findProperty("version") as String
description = "Enhanced proxy management for Velocity"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    implementation("org.bstats:bstats-velocity:3.0.2")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("velocity-plugin.json") {
            expand(
                "version" to project.version,
                "description" to project.description
            )
        }
    }

    shadowJar {
        archiveFileName.set("${findProperty("plugin-name") as String} v${project.version}.jar")
        archiveClassifier.set(null as String?)

        relocate("org.bstats", "dev.lenvx.betterenhancedvelocity.bstats")
        relocate("org.spongepowered", "dev.lenvx.betterenhancedvelocity.spongepowered")

        minimize()
    }

    jar {
        archiveFileName.set("${findProperty("plugin-name") as String} v${project.version}-unshaded.jar")
    }

    withType<Jar> {
        destinationDirectory.set(file("$rootDir/bin/"))
    }
}
