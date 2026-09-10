package xyz.lunalna.modifierkeybinds.legacy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lunalna.modifierkeybinds.legacy.mixin.KeyBindingAccessor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Mod(modid = "modifier_keybinds", name = "Modifier Keybinds", version = "1.0.0", clientSideOnly = true, acceptableRemoteVersions = "*")
public final class ModifierKeybinds {
    private static final Logger LOGGER = LogManager.getLogger("modifier_keybinds");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, KeyChord> BINDINGS = new LinkedHashMap<>();
    private static final Map<Integer, List<KeyBinding>> ACTIVE = new HashMap<>();
    private static Path configPath;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        configPath = event.getSuggestedConfigurationFile().toPath().resolveSibling("modifier_keybinds.json");
        if (!Files.exists(configPath)) return;
        try (Reader reader = Files.newBufferedReader(configPath)) {
            Config config = GSON.fromJson(reader, Config.class);
            if (config != null && config.bindings != null) BINDINGS.putAll(config.bindings);
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Could not read {}", configPath, exception);
        }
    }

    public static KeyChord get(KeyBinding key) {
        return BINDINGS.get(key.getKeyDescription());
    }

    public static void set(KeyBinding key, KeyChord chord) {
        BINDINGS.put(key.getKeyDescription(), chord);
        save();
    }

    public static void clear(KeyBinding key) {
        if (BINDINGS.remove(key.getKeyDescription()) != null) save();
    }

    public static boolean keyEvent(int code, boolean down) {
        if (!down) {
            List<KeyBinding> active = ACTIVE.remove(code);
            if (active == null) return false;
            active.forEach(key -> ((KeyBindingAccessor) key).modifierKeybinds$pressed(false));
            return true;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null || mc.gameSettings == null) return false;
        if (ACTIVE.containsKey(code)) return true;
        List<KeyBinding> matches = new ArrayList<>();
        for (KeyBinding key : mc.gameSettings.keyBindings) {
            KeyChord chord = get(key);
            if (chord != null && chord.matches(code)) {
                KeyBindingAccessor accessor = (KeyBindingAccessor) key;
                accessor.modifierKeybinds$pressed(true);
                accessor.modifierKeybinds$pressTime(accessor.modifierKeybinds$pressTime() + 1);
                matches.add(key);
            }
        }
        if (matches.isEmpty()) return false;
        ACTIVE.put(code, matches);
        return true;
    }

    public static boolean consumed(int code) {
        return ACTIVE.containsKey(code);
    }

    public static void releaseAll() {
        ACTIVE.values().forEach(keys -> keys.forEach(key -> ((KeyBindingAccessor) key).modifierKeybinds$pressed(false)));
        ACTIVE.clear();
    }

    private static void save() {
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(new Config(BINDINGS), writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Could not save {}", configPath, exception);
        }
    }

    private static final class Config {
        private final Map<String, KeyChord> bindings;

        private Config(Map<String, KeyChord> bindings) {
            this.bindings = bindings;
        }
    }
}
