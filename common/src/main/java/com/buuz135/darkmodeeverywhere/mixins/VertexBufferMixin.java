package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

    @Inject(method="_drawWithShader", at=@At(value="HEAD"))
    private void _drawWithShader(Matrix4f p_253705_, Matrix4f p_253737_, ShaderInstance p_166879_, CallbackInfo ci) {
        if (ClientProxy.getSelectedShaderValue() == null) return;
        if (p_166879_ != ClientProxy.getSelectedTexShader() && p_166879_ != ClientProxy.getSelectedTexColorShader()) return;

        Uniform divideFactor = p_166879_.getUniform("DivideFactor");
        if (divideFactor == null) return;

        divideFactor.set(ClientProxy.getSelectedShaderValue().divideFactor);
    }
}
