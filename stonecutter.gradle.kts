plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2.x-fabric"

tasks.register("chiseledBuild") {
    group = "build"
    description = "Builds and collects every supported Minecraft and loader target"
    dependsOn(
        ":1.8.9-forge:buildAndCollect",
        ":1.12.2-forge:buildAndCollect",
        ":1.16.5-fabric:buildAndCollect",
        ":1.16.5-forge:buildAndCollect",
        ":1.18.2-fabric:buildAndCollect",
        ":1.18.2-forge:buildAndCollect",
        ":1.19.2-fabric:buildAndCollect",
        ":1.19.2-forge:buildAndCollect",
        ":1.20.1-fabric:buildAndCollect",
        ":1.20.1-forge:buildAndCollect",
        ":1.21.4-fabric:buildAndCollect",
        ":1.21.4-neoforge:buildAndCollect",
        ":1.21.8-fabric:buildAndCollect",
        ":1.21.8-neoforge:buildAndCollect",
        ":1.21.1-fabric:buildAndCollect",
        ":1.21.1-neoforge:buildAndCollect",
        ":1.21.11-fabric:buildAndCollect",
        ":1.21.11-neoforge:buildAndCollect",
        ":26.2.x-fabric:buildAndCollect",
        ":26.2.x-neoforge:buildAndCollect"
    )
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
