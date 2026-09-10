package xyz.lunalna.modifierkeybinds.legacy;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.Name("Modifier Keybinds")
@IFMLLoadingPlugin.TransformerExclusions("xyz.lunalna.modifierkeybinds.legacy.LoadingPlugin")
public final class LoadingPlugin implements IFMLLoadingPlugin {
    public LoadingPlugin() {
        MixinBootstrap.init();
        Mixins.addConfiguration("modifier_keybinds.mixins.json");
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
