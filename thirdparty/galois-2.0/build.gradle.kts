plugins {
    `java`
}

group = "edu.utexas.oden.iss"
version = "2.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.sf.trove4j:trove4j:3.0.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
}

tasks.withType<JavaCompile>() {
    options.isWarnings = false
}
