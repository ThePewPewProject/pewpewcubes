plugins {
    kotlin("jvm") version "2.0.21"
}

group = "de.pewpewproject.pewpewcubes"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}