plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    `maven-publish`
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

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/eferraris/download-manager")
            credentials {
                username = "eferraris"
                password = project.findProperty("gpr.key") as String? ?: System.getenv("PAT")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
