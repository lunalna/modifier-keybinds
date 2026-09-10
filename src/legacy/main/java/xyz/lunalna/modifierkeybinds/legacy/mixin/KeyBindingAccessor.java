package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("pressed")
    void modifierKeybinds$pressed(boolean pressed);

    @Accessor("pressTime")
    int modifierKeybinds$pressTime();

    @Accessor("pressTime")
    void modifierKeybinds$pressTime(int ticks);
}
