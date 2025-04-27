package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.buuz135.darkmodeeverywhere.DarkShaderInstance;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

    @Inject(method="_drawWithShader", at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"))
    private void darkModeEverywhere$_drawWithShader(Matrix4f p_253705_, Matrix4f p_253737_, ShaderInstance p_166879_, CallbackInfo ci) {
        if (!(p_166879_ instanceof DarkShaderInstance darkShaderInstance)) return;

        if (darkShaderInstance.DivideFactor == null) return;

        darkShaderInstance.DivideFactor.set(ClientProxy.getSelectedShaderValue().divideFactor);
    }
}
