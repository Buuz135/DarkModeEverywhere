package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClassUtil;
import com.buuz135.darkmodeeverywhere.ClientProxy;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(GameRenderer.class)
public abstract class GameRenderMixin {

    @Unique
    private static void darkModeEverywhere$replaceDefaultShader(CallbackInfoReturnable<ShaderInstance> cir, Supplier<ShaderInstance> replacer) {
        ShaderInstance replacement = replacer.get();
        if (replacement == null) return;
        cir.setReturnValue(replacer.get());
    }

    @Unique
    private static void darkModeEverywhere$replaceDefaultShaderWhenAppropriate(CallbackInfoReturnable<ShaderInstance> cir, Supplier<ShaderInstance> replacer) {
        if (ClientProxy.SELECTED_SHADER_VALUE == null) return;

        var callerClassName = ClassUtil.getCallerClassName();
        if (callerClassName == null) {
            darkModeEverywhere$replaceDefaultShader(cir, replacer);
            return;
        }

        boolean elementNameIsBlacklisted = ClientProxy.isElementNameBlacklisted(callerClassName);

        if (!elementNameIsBlacklisted) {
            darkModeEverywhere$replaceDefaultShader(cir, replacer);
        }
    }

    @Inject(method = "getPositionTexShader", at = @At("HEAD"), cancellable = true)
    private static void getPositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        darkModeEverywhere$replaceDefaultShaderWhenAppropriate(cir, ClientProxy::getSelectedTexShader);
    }

    @Inject(method = "getPositionTexColorShader", at = @At("HEAD"), cancellable = true)
    private static void getPositionTexColorShader(CallbackInfoReturnable<ShaderInstance> cir) {
        darkModeEverywhere$replaceDefaultShaderWhenAppropriate(cir, ClientProxy::getSelectedTexColorShader);
    }
}
