package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.DarkRenderPipelines;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ShaderManager.class)
public class ShaderManagerMixin {

    @Inject(method = "apply", at = @At("HEAD"))
    private void darkModeEverywhere$beforeShaderReload(CallbackInfo ci) {
        DarkRenderPipelines.setLoaded(false);
    }

    @ModifyExpressionValue(
        method = "lambda$reload$1",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderPipelines;requiredPipelines()Ljava/util/List;"
        )
    )
    private static List<RenderPipeline> darkModeEverywhere$includeDarkPipelines(List<RenderPipeline> original) {
        List<RenderPipeline> pipelines = new ArrayList<>(RenderPipelines.requiredPipelines());
        pipelines.addAll(DarkRenderPipelines.all());
        return pipelines;
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void darkModeEverywhere$afterShaderReload(CallbackInfo ci) {
        DarkRenderPipelines.setLoaded(true);
    }
}
