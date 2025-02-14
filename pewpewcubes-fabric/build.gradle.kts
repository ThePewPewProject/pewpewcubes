import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("pewpewcubes-convention")

    alias(libs.plugins.loom)
}

val modId: String by project

val version = libs.versions.pewpewcubes.get()
val yarnVersion = libs.versions.yarn.get()

base {
    archivesName = "pewpewcubes-fabric"
}

repositories {

}

dependencies {
    minecraft(libs.minecraft)
    mappings("net.fabricmc:yarn:${yarnVersion}:v2")
    modImplementation(libs.fabric)
    modImplementation(libs.fabric.api)
    modImplementation(libs.geckolib)

    //compileOnly(project(":common"))
}

loom {
    accessWidenerPath = file("src/main/resources/pewpewcubes.accesswidener")

    mixin.defaultRefmapName.set("${modId}.refmap.json")

    runs {
        named("client") {
            configName = "Fabric Client"

            client()
            ideConfigGenerated(true)
            runDir("runs/" + name)
            programArg("--username=Dev")
        }

        named("server") {
            configName = "Fabric Server"

            server()
            ideConfigGenerated(true)
            runDir("runs/" + name)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    //source(project(":common").sourceSets.getByName("main").allSource)
}

tasks.named<Jar>("sourcesJar").configure {
    //from(project(":common").sourceSets.getByName("main").allSource)
}

tasks.withType<Javadoc>().configureEach {
    //source(project(":common").sourceSets.getByName("main").allJava)
}

tasks.withType<ProcessResources>().configureEach {
    //from(project(":common").sourceSets.getByName("main").resources)
    exclude("**/accesstransformer-nf.cfg")
}

publishing {
    publications {
        create<MavenPublication>("pewpewcubes") {
            from(components["java"])
            artifactId = base.archivesName.get()
        }
    }
}