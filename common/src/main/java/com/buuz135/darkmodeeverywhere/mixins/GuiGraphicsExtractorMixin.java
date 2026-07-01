package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClassUtil;
import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.buuz135.darkmodeeverywhere.DarkRenderPipelines;
import com.buuz135.darkmodeeverywhere.ShaderConfig;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    @ModifyVariable(
            method = {"fill", "blit", "blitSprite", "innerFill", "innerBlit", "innerTiledBlit"},
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0,
            require = 0
    )
    private RenderPipeline darkModeEverywhere$replaceGuiPipeline(RenderPipeline pipeline) {
        ShaderConfig.ShaderValue shaderValue = ClientProxy.getSelectedShaderValue();
        if (shaderValue == null) {
            return pipeline;
        }

        String callerClassName = ClassUtil.getCallerClassName();
        if (callerClassName != null && ClientProxy.isElementNameBlacklisted(callerClassName)) {
            return pipeline;
        }

        return DarkRenderPipelines.replace(pipeline, shaderValue);
    }
}
