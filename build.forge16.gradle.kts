buildscript {
    repositories { maven("https://maven.architectury.dev") }
    dependencies { classpath("dev.architectury:architectury-pack200:0.1.3") }
}
plugins { id("gg.essential.loom") version "1.15.50" }

version = "1.0.0+1.16.5"
group = "xyz.lunalna"
base.archivesName = "modifier_keybinds-forge"
loom {
    runs.configureEach { ideConfigGenerated(false) }
    mixin.useLegacyMixinAp.set(true)
    mixin.defaultRefmapName.set("modifier_keybinds.refmap.json")
    forge.mixinConfig("modifier_keybinds.mixins.json")
}
dependencies {
    minecraft("com.mojang:minecraft:1.16.5")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:1.16.5-36.2.42")
}
sourceSets.main {
    resources.setSrcDirs(listOf(rootProject.file("src/main/resources")))
}
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks.processResources {
    filesMatching("pack.mcmeta") { expand("pack_format" to 6) }
    filesMatching("META-INF/mods.toml") {
        expand(
            "id" to "modifier_keybinds",
            "name" to "Modifier Keybinds",
            "version" to "1.0.0",
            "minecraft" to "[1.16.5]"
        )
        filter { it.replace("[40,)", "[36,)") }
    }
    filesMatching("*.mixins.json") { expand("java" to "JAVA_8") }
    exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
}
tasks.jar {
    manifest.attributes("MixinConfigs" to "modifier_keybinds.mixins.json")
    from(rootProject.file("LICENSE"))
}
tasks.register<Copy>("buildAndCollect") {
    from(tasks.named("remapJar"), tasks.named("remapSourcesJar"))
    into(rootProject.file("build/libs/1.0.0"))
}

tasks.named("generateDLIConfig") {
    notCompatibleWithConfigurationCache("Legacy Loom accesses Project while generating the launch configuration")
}
