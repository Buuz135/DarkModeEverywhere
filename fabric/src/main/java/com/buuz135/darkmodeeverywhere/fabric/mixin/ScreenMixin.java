package com.buuz135.darkmodeeverywhere.fabric.mixin;

import com.buuz135.darkmodeeverywhere.FabricDarkModeEverywhereClient;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "init(II)V", at = @At("TAIL"))
    private void darkmodeeverywhere$afterInit(int width, int height, CallbackInfo ci) {
        if (FabricDarkModeEverywhereClient.clientProxy == null) {
            return;
        }
        FabricDarkModeEverywhereClient.clientProxy.openGui(
                (Screen) (Object) this,
                button -> ((ScreenAccessor) (Object) this).darkmodeeverywhere$addRenderableWidget(button)
        );
    }
}
