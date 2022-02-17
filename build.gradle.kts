plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
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
