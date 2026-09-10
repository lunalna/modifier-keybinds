package xyz.lunalna.modifierkeybinds.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class Screens {
    private Screens() {
    }

    public static boolean hasScreen() {
        return current() != null;
    }

    public static Screen current() {
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.2 {
        return minecraft.gui == null ? null : minecraft.gui.screen();
        //?} else {
        /*return minecraft.screen;
         *///?}
    }
}
