import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.40"
    id("com.diffplug.gradle.spotless") version "3.23.1"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "icfp2019"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(platform("org.junit:junit-bom:5.4.0"))
    implementation(project(":thirdparty:galois-2.0"))

    implementation("ca.umontreal.iro.simul:ssj:3.3.1")
    implementation("com.google.guava:guava:28.0-jre")
    implementation("org.jgrapht:jgrapht-core:1.3.0")
    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.junit.jupiter:junit-jupiter-api")
    implementation("org.junit.jupiter:junit-jupiter-engine")
    implementation("org.pcollections:pcollections:3.0.3")
}

spotless {
     kotlin {
        ktlint("0.33.0")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.allWarningsAsErrors = true
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    failFast = true
}

tasks.register("run", JavaExec::class) {
    classpath = sourceSets.getByName("main").runtimeClasspath

    main = "icfp2019.AppKt"
    args = listOf("problems/prob-003.desc")
}

tasks.register("runAll", JavaExec::class) {
    classpath = sourceSets.getByName("main").runtimeClasspath

    main = "icfp2019.AppKt"
    args = listOf("problems")
}

tasks.register<Zip>("packageDistribution") {
    archiveFileName.set("solutions.zip")
    destinationDirectory.set(file("$buildDir/dist"))

    from("problems")
    include("*.sol")

}
