package xyz.lunalna.modifierkeybinds;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public final class KeyChord {
    private final int keyCode;
    private final int modifiers;
    private static final int SUPPORTED_MODIFIERS =
            GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SUPER;
    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    private static final boolean MACOS = OS_NAME.contains("mac") || OS_NAME.contains("darwin");
    private static final boolean WINDOWS = OS_NAME.startsWith("windows");

    public KeyChord(int keyCode, int modifiers) {
        this.keyCode = keyCode;
        this.modifiers = modifiers & SUPPORTED_MODIFIERS;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof KeyChord)) return false;
        KeyChord chord = (KeyChord) other;
        return keyCode == chord.keyCode && modifiers == chord.modifiers;
    }

    @Override
    public int hashCode() {
        return 31 * keyCode + modifiers;
    }

    public boolean matches(int pressedKey, int pressedModifiers) {
        return keyCode == pressedKey && modifiers == (pressedModifiers & SUPPORTED_MODIFIERS);
    }

    public static boolean hasModifiers(int modifiers) {
        return (modifiers & SUPPORTED_MODIFIERS) != 0;
    }

    public String displayName() {
        StringBuilder name = new StringBuilder();
        append(name, modifiers, GLFW.GLFW_MOD_CONTROL, MACOS ? "Control" : "Ctrl");
        append(name, modifiers, GLFW.GLFW_MOD_ALT, MACOS ? "Option" : "Alt");
        append(name, modifiers, GLFW.GLFW_MOD_SHIFT, "Shift");
        append(name, modifiers, GLFW.GLFW_MOD_SUPER, MACOS ? "Command" : WINDOWS ? "Windows" : "Super");
        name.append(InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString());
        return name.toString();
    }

    public static boolean isModifierKey(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
                || keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT
                || keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
                || keyCode == GLFW.GLFW_KEY_LEFT_SUPER || keyCode == GLFW.GLFW_KEY_RIGHT_SUPER;
    }

    private static void append(StringBuilder name, int modifiers, int modifier, String label) {
        if ((modifiers & modifier) != 0) {
            name.append(label).append('+');
        }
    }
}
