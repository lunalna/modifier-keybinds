package xyz.lunalna.modifierkeybinds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xyz.lunalna.modifierkeybinds.client.Screens;
import xyz.lunalna.modifierkeybinds.mixin.KeyMappingAccessor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ModifierKeybinds {
    public static final String ID = "modifier_keybinds";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, KeyChord> BINDINGS = new LinkedHashMap<>();
    private static final Map<Integer, List<KeyMapping>> ACTIVE = new HashMap<>();
    private static Path configPath;
    private static int eventModifiers;

    private ModifierKeybinds() {
    }

    public static void init() {
        init(Minecraft.getInstance().gameDirectory.toPath().resolve("config"));
    }

    public static void init(Path configDirectory) {
        configPath = configDirectory.resolve(ID + ".json");
        load();
    }

    public static KeyChord getBinding(KeyMapping mapping) {
        return BINDINGS.get(mapping.getName());
    }

    public static void setBinding(KeyMapping mapping, KeyChord chord) {
        if (!chord.equals(BINDINGS.put(mapping.getName(), chord))) {
            save();
        }
    }

    public static void clearBinding(KeyMapping mapping) {
        if (BINDINGS.remove(mapping.getName()) != null) {
            save();
        }
    }

    public static void clearAllBindings() {
        if (!BINDINGS.isEmpty()) {
            BINDINGS.clear();
            save();
        }
    }

    public static void setEventModifiers(int modifiers) {
        eventModifiers = modifiers;
    }

    public static int getEventModifiers() {
        return eventModifiers;
    }

    public static boolean onKeyEvent(int keyCode, int action, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (action == GLFW.GLFW_RELEASE) {
            List<KeyMapping> mappings = ACTIVE.remove(keyCode);
            if (mappings == null) {
                return false;
            }
            mappings.forEach(mapping -> mapping.setDown(false));
            return true;
        }

        if (Screens.hasScreen() || minecraft.options == null) {
            return false;
        }

        if (action != GLFW.GLFW_PRESS) {
            return ACTIVE.containsKey(keyCode);
        }

        List<KeyMapping> matches = new ArrayList<>();
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            KeyChord chord = getBinding(mapping);
            if (chord != null && chord.matches(keyCode, modifiers)) {
                matches.add(mapping);
                KeyMappingAccessor accessor = (KeyMappingAccessor) mapping;
                mapping.setDown(true);
                accessor.modifierKeybinds$setClickCount(accessor.modifierKeybinds$getClickCount() + 1);
            }
        }

        if (matches.isEmpty()) {
            return false;
        }
        ACTIVE.put(keyCode, matches);
        return true;
    }

    public static void releaseActiveBindings() {
        ACTIVE.values().stream().flatMap(List::stream).distinct()
                .forEach(mapping -> ((KeyMappingAccessor) mapping).modifierKeybinds$setDown(false));
        ACTIVE.clear();
    }

    private static void load() {
        BINDINGS.clear();
        if (!Files.exists(configPath)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(configPath)) {
            Config config = GSON.fromJson(reader, Config.class);
            if (config != null && config.bindings != null) {
                BINDINGS.putAll(config.bindings);
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Could not read {}", configPath, exception);
        }
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
