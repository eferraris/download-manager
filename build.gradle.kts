plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    `maven-publish`
}

group="com.eferraris"
version="1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation( platform("com.amazonaws:aws-java-sdk-bom:1.12.159") )
    implementation("com.amazonaws:aws-java-sdk-s3")
    implementation("commons-io:commons-io:2.11.0")
    implementation("ch.qos.logback:logback-classic:1.2.10")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/eferraris/download-manager")
            credentials {
                username = "eferraris"
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
