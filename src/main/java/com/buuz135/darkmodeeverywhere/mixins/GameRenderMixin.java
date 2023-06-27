package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClassUtil;
import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.mojang.blaze3d.systems.RenderSystem;
import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRenderMixin {


    private static void replaceDefaultShaderWithSelectedShader(CallbackInfoReturnable<ShaderInstance> cir) {
        cir.setReturnValue(ClientProxy.REGISTERED_SHADERS.get(ClientProxy.SELECTED_SHADER));
    }

    @Inject(method = "getPositionTexShader", at = @At("HEAD"), cancellable = true)
    private static void getPositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ClientProxy.SELECTED_SHADER != null){
            var callerClassName = ClassUtil.getCallerClassName();
            if (callerClassName == null) {
                replaceDefaultShaderWithSelectedShader(cir);
                return;
            }

            boolean elementNameIsBlacklisted = ClientProxy.isElementNameBlacklisted(callerClassName);

            if (!elementNameIsBlacklisted) {
                replaceDefaultShaderWithSelectedShader(cir);
            }
        }
    }


}
