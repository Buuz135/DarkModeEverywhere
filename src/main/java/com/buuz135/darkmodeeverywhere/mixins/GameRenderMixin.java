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

import java.util.stream.Stream;

import java.lang.StackWalker;
import java.lang.StackWalker.StackFrame;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    private static final String SET_SHADER_METHOD_NAME = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_157427_");

    private static void replaceDefaultShaderWithSelectedShader(CallbackInfoReturnable<ShaderInstance> cir) {
        cir.setReturnValue(ClientProxy.REGISTERED_SHADERS.get(ClientProxy.SELECTED_SHADER));
    }

    @Inject(method = "getPositionTexShader", at = @At("HEAD"), cancellable = true)
    private static void getPositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ClientProxy.SELECTED_SHADER != null){
            var callerClassName = getCallerClassName();
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

    private static String walkForCallerClassName(Stream<StackFrame> stackFrameStream) {
        return stackFrameStream
            .filter(frame -> !(
                frame.getClassName().equals(GameRenderer.class.getName())
                || frame.getClassName().startsWith("java.lang.Thread")
                || frame.getMethodName().equals(SET_SHADER_METHOD_NAME)
                || frame.getClassName().equals(GuiComponent.class.getName())
            ))
            .findFirst()
            .map(f -> f.getClassName() + ":" + f.getMethodName())
            .orElse(null);
    }

    private static String getCallerClassName() {
        return StackWalker.getInstance()
            .walk(GameRenderMixin::walkForCallerClassName);
    }
}
