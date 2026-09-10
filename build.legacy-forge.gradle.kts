buildscript {
    repositories { maven("https://maven.architectury.dev") }
    dependencies { classpath("dev.architectury:architectury-pack200:0.1.3") }
}
plugins { id("gg.essential.loom") version "1.15.50" }

val mc = project.name.removeSuffix("-forge")
val legacyVersion = "1.0.0"
version = "$legacyVersion+$mc"
group = "xyz.lunalna"
base.archivesName = "modifier_keybinds-forge"

val bundledMixin = configurations.create("bundledMixin")
repositories { maven("https://repo.spongepowered.org/maven") }

dependencies {
    minecraft("com.mojang:minecraft:$mc")
    mappings("de.oceanlabs.mcp:mcp_stable:${if (mc == "1.8.9") "22-1.8.9" else "39-1.12"}")
    "forge"("net.minecraftforge:forge:${if (mc == "1.8.9") "1.8.9-11.15.1.2318-1.8.9" else "1.12.2-14.23.5.2847"}")
    implementation("org.spongepowered:mixin:0.8.7")
    annotationProcessor("org.spongepowered:mixin:0.8.7:processor")
    bundledMixin("org.spongepowered:mixin:0.8.7") { isTransitive = false }
}
loom {
    runs.configureEach { ideConfigGenerated(false) }
    mixin.useLegacyMixinAp.set(true)
    forge.pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    mixin.defaultRefmapName.set("modifier_keybinds.refmap.json")
    runs.named("client") {
        property("fml.coreMods.load", "xyz.lunalna.modifierkeybinds.legacy.LoadingPlugin")
        runDir("run/$mc")
    }
}
sourceSets.main {
    java.setSrcDirs(listOf(rootProject.file("src/legacy/main/java"), rootProject.file("src/legacy/$mc/java")))
    resources.setSrcDirs(listOf(rootProject.file("src/legacy/main/resources")))
}
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks.processResources {
    val minecraftVersion = mc
    val modVersion = legacyVersion
    inputs.property("minecraft", mc)
    inputs.property("version", legacyVersion)
    filesMatching("mcmod.info") { expand("version" to modVersion, "minecraft" to minecraftVersion) }
    filesMatching("modifier_keybinds.mixins.json") {
        filter {
            if (minecraftVersion == "1.12.2") it.replace(
                "\"KeyEntryMixin\"",
                "\"KeyEntryMixin\", \"ForgeKeyBindingMixin\""
            ) else it.replace("\"KeyEntryMixin\"", "\"KeyEntryMixin\", \"MinecraftMixin\", \"GuiControlsResetMixin\"")
        }
    }
}
tasks.jar {
    from(bundledMixin.map { zipTree(it) })
    exclude(
        "org/spongepowered/asm/launch/MixinLaunchPlugin.class",
        "org/spongepowered/asm/launch/MixinTransformationService.class",
        "org/spongepowered/asm/launch/platform/container/ContainerHandleModLauncherEx*.class"
    )
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA", "module-info.class")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest.attributes(
        "FMLCorePlugin" to "xyz.lunalna.modifierkeybinds.legacy.LoadingPlugin",
        "FMLCorePluginContainsFMLMod" to "true", "ForceLoadAsMod" to "true"
    )
    from(rootProject.file("LICENSE"))
}
tasks.register<Copy>("buildAndCollect") {
    from(tasks.named("remapJar"), tasks.named("remapSourcesJar"))
    into(rootProject.file("build/libs/$legacyVersion"))
}

tasks.named("generateDLIConfig") {
    notCompatibleWithConfigurationCache("Legacy Loom accesses Project while generating the launch configuration")
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    })
}
