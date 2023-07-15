package com.buuz135.darkmodeeverywhere;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class DarkShaderInstance extends ShaderInstance {

    public final Uniform DivideFactor;
    public final Uniform PerceptionScale;

    public DarkShaderInstance(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_) throws IOException {
        super(p_173336_, shaderLocation, p_173338_);
        this.DivideFactor = this.getUniform("DivideFactor");
        this.PerceptionScale = this.getUniform("PerceptionScale");
    }
}
