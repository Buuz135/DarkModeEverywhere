package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.buuz135.darkmodeeverywhere.DarkShaderInstance;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.ByteBuffer;

@Mixin(BufferUploader.class)
public class BufferUploaderMixin {
    @Inject(method="_end", locals=LocalCapture.CAPTURE_FAILHARD, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"))
    private static void _end(ByteBuffer p_166839_, VertexFormat.Mode p_166840_, VertexFormat p_166841_, int p_166842_, VertexFormat.IndexType p_166843_, int p_166844_, boolean p_166845_, CallbackInfo ci, int i, int j, ShaderInstance shaderInstance) {
        if (!(shaderInstance instanceof DarkShaderInstance darkShaderInstance)) return;
        if (darkShaderInstance.DivideFactor == null) return;
        darkShaderInstance.DivideFactor.set(ClientProxy.getSelectedShaderValue().divideFactor);
    }
}
