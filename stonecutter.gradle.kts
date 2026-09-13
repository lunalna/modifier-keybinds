plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2.x-fabric"

val supportedTargets = listOf(
    "1.8.9-forge",
    "1.12.2-forge",
    "1.16.5-fabric",
    "1.16.5-forge",
    "1.18.2-fabric",
    "1.18.2-forge",
    "1.19.2-fabric",
    "1.19.2-forge",
    "1.20.1-fabric",
    "1.20.1-forge",
    "1.21.4-fabric",
    "1.21.4-neoforge",
    "1.21.8-fabric",
    "1.21.8-neoforge",
    "1.21.1-fabric",
    "1.21.1-neoforge",
    "1.21.11-fabric",
    "1.21.11-neoforge",
    "26.2.x-fabric",
    "26.2.x-neoforge"
)

tasks.register("chiseledBuild") {
    group = "build"
    description = "Builds and collects every supported Minecraft and loader target"
    dependsOn(supportedTargets.map { ":$it:buildAndCollect" })
}

tasks.register("publishModrinth") {
    group = "publishing"
    description = "Builds and publishes every supported Minecraft and loader target to Modrinth"
    dependsOn(supportedTargets.map { ":$it:modrinth" })
}

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    // Makes version- and loader-specific properties apply from `stoncutter.properties.toml`
    properties {
        tags(version, loader)
    }

    // Adds constants to Stonecutter comments (i.e. for `//? if fabric {...`)
    constants {
        match(loader, "fabric", "neoforge", "forge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = properties.get<String>("mod.id") != "template"
    dependencies["fapi"] = properties.getOrNull<String>("deps.fabric_api") ?: "0"

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v2 named", "classTweaker v2 official")
        }
    }
}
