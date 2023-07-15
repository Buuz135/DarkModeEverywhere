package com.buuz135.darkmodeeverywhere;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class DarkShaderInstance extends ShaderInstance {

    @Nullable
    public final Uniform DivideFactor;
    @Nullable
    public final Uniform PerceptionScale;

    public DarkShaderInstance(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_) throws IOException {
        super(p_173336_, shaderLocation, p_173338_);
        this.DivideFactor = this.getUniform("DivideFactor");
        this.PerceptionScale = this.getUniform("PerceptionScale");
    }
}
