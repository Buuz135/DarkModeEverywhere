package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.DarkRenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ShaderManager.class)
public class ShaderManagerMixin {

    @Inject(method = "apply", at = @At("HEAD"))
    private void darkModeEverywhere$beforeShaderReload(CallbackInfo ci) {
        DarkRenderPipelines.setLoaded(false);
    }

    @Redirect(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderPipelines;getStaticPipelines()Ljava/util/List;")
    )
    private List<RenderPipeline> darkModeEverywhere$includeDarkPipelines() {
        List<RenderPipeline> pipelines = new ArrayList<>(RenderPipelines.getStaticPipelines());
        pipelines.addAll(DarkRenderPipelines.all());
        return pipelines;
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void darkModeEverywhere$afterShaderReload(CallbackInfo ci) {
        DarkRenderPipelines.setLoaded(true);
    }
}
