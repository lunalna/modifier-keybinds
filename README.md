# Modifier Keybinds

This simple mod allows you to use keybind chords such as `Ctrl+J`, `Alt+A`, or `Ctrl+Meta+F` as bindings for your
Minecraft keybinds. You can assign any combination of modifier keys (Ctrl, Alt, Shift, Meta) and a regular key to a
keybind. You can also use modifier keys in combination with mouse buttons.

This mod hooks into the **Options → Controls → Key Binds** screen, so you can continue to manage your minecraft keybinds
like usual. Please note that some mods that change the behavior how they handle binding their keybinds may not work as
expected. If you find a mod that does this, please open an issue on GitHub.

## Supported versions

| Minecraft                             | Loaders          |
|---------------------------------------|------------------|
| 1.8.9, 1.12.2                         | Forge            |
| 1.16.5, 1.18.2, 1.19.2, 1.20.1        | Fabric, Forge    |
| 1.21.1, 1.21.4, 1.21.8, 1.21.11, 26.2 | Fabric, NeoForge |

# Contributing

If you have any feature requests, or you have found a bug or incompatibility, please open an issue on GitHub. Pull
requests are welcome. Please remember to reset your stonecutter version to `26.2.x-fabric` before committing your
changes.

## Building

You can build the mod using Gradle and Stonecutter.

```shell
./gradlew chiseledBuild # to build all versions
```

The finished jars are in `build/libs/1.0.0/`.

Minecraft 1.8.9 and 1.12.2 require Java 8 to run.
