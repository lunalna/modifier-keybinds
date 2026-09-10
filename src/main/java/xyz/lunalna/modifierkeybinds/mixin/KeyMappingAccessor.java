package xyz.lunalna.modifierkeybinds.mixin;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor("isDown")
    void modifierKeybinds$setDown(boolean down);

    @Accessor("clickCount")
    int modifierKeybinds$getClickCount();

    @Accessor("clickCount")
    void modifierKeybinds$setClickCount(int clicks);
}
