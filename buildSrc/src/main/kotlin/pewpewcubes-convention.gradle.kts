plugins {
    `java`
    `maven-publish`
    `idea`
    `eclipse`
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)

    withSourcesJar()
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {

}

val libs = project.versionCatalogs.find("libs")

val modId: String by project
val modDisplayName: String by project
val modAuthors: String by project
val modContributors: String by project
val modLicense: String by project
val modDescription: String by project
val modVersion = libs.get().findVersion("pewpewcubes").get()
val mcVersion = libs.get().findVersion("minecraft").get()
val mcVersionRange = libs.get().findVersion("minecraft.range").get()
val fapiVersion = libs.get().findVersion("fabric.api").get()
val fapiVersionRange = libs.get().findVersion("fabric.api.range").get()
val fabricVersion = libs.get().findVersion("fabric").get()
val fabricVersionRange = libs.get().findVersion("fabric.range").get()

tasks.withType<Jar>().configureEach {
    from(rootProject.file("COPYING")) {
        rename { "${it}_${modDisplayName}" }
    }

    manifest {
        attributes(mapOf(
            "Specification-Title"     to modDisplayName,
            "Specification-Vendor"    to modAuthors,
            "Specification-Version"   to modVersion,
            "Implementation-Title"    to modDisplayName,
            "Implementation-Version"  to modVersion,
            "Implementation-Vendor"   to modAuthors,
            "Built-On-Minecraft"      to mcVersion,
            "MixinConfigs"            to "$modId.mixins.json"
        ))
    }
}

tasks.withType<JavaCompile>().configureEach {
    this.options.encoding = "UTF-8"
    this.options.getRelease().set(17)
}

tasks.withType<ProcessResources>().configureEach {
    val expandProps = mapOf(
        "version" to modVersion,
        "group" to project.group,
        "minecraft_version" to mcVersion,
        "minecraft_version_range" to mcVersionRange,
        "fabric_api_version" to fapiVersion,
        "fabric_api_version_range" to fapiVersionRange,
        "fabric_loader_version" to fabricVersion,
        "fabric_loader_version_range" to fabricVersionRange,
        "mod_display_name" to modDisplayName,
        "mod_authors" to modAuthors,
        "mod_contributors" to modContributors,
        "mod_id" to modId,
        "mod_license" to modLicense,
        "mod_description" to modDescription,
    )

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }

    inputs.properties(expandProps)
}

publishing {
    repositories {
        mavenLocal()
    }
}