package xyz.lunalna.modifierkeybinds;

//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(ModifierKeybinds.ID)
public final class ModifierKeybindsForge {
    @Mod.EventBusSubscriber(modid = ModifierKeybinds.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Client {
        @SubscribeEvent
        public static void setup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ModifierKeybinds.init());
        }
    }
}
*///?}
