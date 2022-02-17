plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    `maven-publish` apply false
}

group="com.eferraris"
version="1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation( platform("com.amazonaws:aws-java-sdk-bom:1.12.159") )
    implementation("com.amazonaws:aws-java-sdk-s3")
    implementation("commons-io:commons-io:2.11.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")
}

subprojects {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {

            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/eferraris/download-manager")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
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
}