package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRenderMixin {

    @Inject(method = "getPositionTexShader", at = @At("HEAD"), cancellable = true)
    private static void getPositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ClientProxy.SELECTED_SHADER != null){
            var element = getCallerCallerClassName();
            if (element == null) {
                cir.setReturnValue(ClientProxy.REGISTERED_SHADERS.get(ClientProxy.SELECTED_SHADER));
                return;
            }

            var elementName = element.getClassName() + ":" + element.getMethodName();
            boolean elementNameIsBlacklisted = ClientProxy.isElementNameBlacklisted(elementName);

            if (!elementNameIsBlacklisted) {
                cir.setReturnValue(ClientProxy.REGISTERED_SHADERS.get(ClientProxy.SELECTED_SHADER));
            }
        }
    }

    private static StackTraceElement getCallerCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(GameRenderer.class.getName())
                    && ste.getClassName().indexOf("java.lang.Thread")!=0
                    && !ste.getMethodName().equals("setShader")
                    && !ste.getClassName().equals(GuiComponent.class.getName())) {
                return ste;
            }
        }
        return null;
    }
}
