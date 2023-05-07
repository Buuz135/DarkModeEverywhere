package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    private static final String SET_SHADER_METHOD_NAME = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_157427_");

    private static void replaceDefaultShaderWithSelectedShader(CallbackInfoReturnable<ShaderInstance> cir) {
        cir.setReturnValue(ClientProxy.REGISTERED_SHADERS.get(ClientProxy.SELECTED_SHADER));
    }

    @Inject(method = "getPositionTexShader", at = @At("HEAD"), cancellable = true)
    private static void getPositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ClientProxy.SELECTED_SHADER != null){
            var element = getCallerCallerClassName();
            if (element == null) {
                replaceDefaultShaderWithSelectedShader(cir);
                return;
            }

            var elementName = element.getClassName() + ":" + element.getMethodName();
            boolean elementNameIsBlacklisted = ClientProxy.isElementNameBlacklisted(elementName);

            if (!elementNameIsBlacklisted) {
                replaceDefaultShaderWithSelectedShader(cir);
            }
        }
    }

    private static StackTraceElement getCallerCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            String className = ste.getClassName();
            if (!className.equals(GameRenderer.class.getName())
                    && className.indexOf("java.lang.Thread") != 0
                    && !ste.getMethodName().equals(SET_SHADER_METHOD_NAME)
                    && !className.equals(GuiComponent.class.getName())) {
                return ste;
            }
        }
        return null;
    }
}
