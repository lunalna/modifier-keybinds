package xyz.lunalna.modifierkeybinds.legacy;

import org.lwjgl.input.Keyboard;

import java.util.Locale;

public final class KeyChord {
    private final int keyCode;
    private final int modifiers;

    public KeyChord(int keyCode, int modifiers) {
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    public int keyCode() {
        return keyCode;
    }

    public boolean matches(int key) {
        return keyCode == key && modifiers == modifiers();
    }

    public static int modifiers() {
        return (down(Keyboard.KEY_LSHIFT, Keyboard.KEY_RSHIFT) ? 1 : 0)
                | (down(Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL) ? 2 : 0)
                | (down(Keyboard.KEY_LMENU, Keyboard.KEY_RMENU) ? 4 : 0)
                | (down(Keyboard.KEY_LMETA, Keyboard.KEY_RMETA) ? 8 : 0);
    }

    public static boolean isModifier(int key) {
        return key == Keyboard.KEY_LSHIFT || key == Keyboard.KEY_RSHIFT
                || key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_RCONTROL
                || key == Keyboard.KEY_LMENU || key == Keyboard.KEY_RMENU
                || key == Keyboard.KEY_LMETA || key == Keyboard.KEY_RMETA;
    }

    private static boolean down(int left, int right) {
        return Keyboard.isKeyDown(left) || Keyboard.isKeyDown(right);
    }

    public String displayName() {
        boolean mac = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");
        StringBuilder name = new StringBuilder();
        if ((modifiers & 2) != 0) name.append(mac ? "Control+" : "Ctrl+");
        if ((modifiers & 4) != 0) name.append(mac ? "Option+" : "Alt+");
        if ((modifiers & 1) != 0) name.append("Shift+");
        if ((modifiers & 8) != 0) name.append(mac ? "Command+" : "Super+");
        return name.append(Keyboard.getKeyName(keyCode)).toString();
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
}
