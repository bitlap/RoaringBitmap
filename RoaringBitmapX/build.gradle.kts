val deps: Map<String, String> by extra

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    `java-library`
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":RoaringBitmap"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${deps["jupiter"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${deps["jupiter"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${deps["jupiter"]}")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
}
