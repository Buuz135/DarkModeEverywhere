package com.buuz135.darkmodeeverywhere;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DarkRenderPipelines {

    private static final float PERFECT_DARK_DIVIDE_FACTOR = 5.5F;
    private static final float LESS_PERFECT_DARK_DIVIDE_FACTOR = 3.5F;
    private static final float TOASTED_LIGHT_DIVIDE_FACTOR = 2.0F;
    private static volatile boolean loaded;

    private static final RenderPipeline[] GUI = new RenderPipeline[]{
            create(RenderPipelines.GUI, "gui_perfect_dark", "core/dark_gui", PERFECT_DARK_DIVIDE_FACTOR, false),
            create(RenderPipelines.GUI, "gui_less_perfect_dark", "core/dark_gui", LESS_PERFECT_DARK_DIVIDE_FACTOR, false),
            create(RenderPipelines.GUI, "gui_toasted_light", "core/dark_gui", TOASTED_LIGHT_DIVIDE_FACTOR, false)
    };
    private static final RenderPipeline[] GUI_TEXTURED = new RenderPipeline[]{
            create(RenderPipelines.GUI_TEXTURED, "gui_textured_perfect_dark", "core/dark_gui_textured", PERFECT_DARK_DIVIDE_FACTOR, false),
            create(RenderPipelines.GUI_TEXTURED, "gui_textured_less_perfect_dark", "core/dark_gui_textured", LESS_PERFECT_DARK_DIVIDE_FACTOR, false),
            create(RenderPipelines.GUI_TEXTURED, "gui_textured_toasted_light", "core/dark_gui_textured", TOASTED_LIGHT_DIVIDE_FACTOR, false)
    };
    private static final RenderPipeline[] GUI_TEXTURED_PREMULTIPLIED_ALPHA = new RenderPipeline[]{
            create(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, "gui_textured_premultiplied_alpha_perfect_dark", "core/dark_gui_textured", PERFECT_DARK_DIVIDE_FACTOR, true),
            create(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, "gui_textured_premultiplied_alpha_less_perfect_dark", "core/dark_gui_textured", LESS_PERFECT_DARK_DIVIDE_FACTOR, true),
            create(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, "gui_textured_premultiplied_alpha_toasted_light", "core/dark_gui_textured", TOASTED_LIGHT_DIVIDE_FACTOR, true)
    };

    private static final List<RenderPipeline> ALL = List.of(
            GUI[0], GUI[1], GUI[2],
            GUI_TEXTURED[0], GUI_TEXTURED[1], GUI_TEXTURED[2],
            GUI_TEXTURED_PREMULTIPLIED_ALPHA[0], GUI_TEXTURED_PREMULTIPLIED_ALPHA[1], GUI_TEXTURED_PREMULTIPLIED_ALPHA[2]
    );

    public static RenderPipeline replace(RenderPipeline pipeline, ShaderConfig.ShaderValue shaderValue) {
        if (!loaded) {
            return pipeline;
        }
        int shaderIndex = getShaderIndex(shaderValue);
        if (shaderIndex < 0) {
            return pipeline;
        }
        if (pipeline == RenderPipelines.GUI) {
            return GUI[shaderIndex];
        }
        if (pipeline == RenderPipelines.GUI_TEXTURED) {
            return GUI_TEXTURED[shaderIndex];
        }
        if (pipeline == RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA) {
            return GUI_TEXTURED_PREMULTIPLIED_ALPHA[shaderIndex];
        }
        return pipeline;
    }

    public static List<RenderPipeline> all() {
        return ALL;
    }

    public static void setLoaded(boolean loaded) {
        DarkRenderPipelines.loaded = loaded;
    }

    private static RenderPipeline create(RenderPipeline source, String name, String fragmentShader, float divideFactor, boolean premultipliedAlpha) {
        RenderPipeline.Builder builder = RenderPipeline.builder()
                .withLocation(Identifier.fromNamespaceAndPath(DarkModeEverywhere.MODID, "pipeline/" + name))
                .withVertexShader(source.getVertexShader())
                .withFragmentShader(Identifier.fromNamespaceAndPath(DarkModeEverywhere.MODID, fragmentShader))
                .withShaderDefine("DME_DIVIDE_FACTOR", divideFactor)
                .withColorTargetState(source.getColorTargetState())
                .withDepthStencilState(Optional.ofNullable(source.getDepthStencilState()))
                .withPolygonMode(source.getPolygonMode())
                .withCull(source.isCull())
                .withVertexFormat(source.getVertexFormat(), source.getVertexFormatMode());

        for (String sampler : source.getSamplers()) {
            builder.withSampler(sampler);
        }
        for (RenderPipeline.UniformDescription uniform : source.getUniforms()) {
            builder.withUniform(uniform.name(), uniform.type());
        }
        if (premultipliedAlpha) {
            builder.withShaderDefine("DME_PREMULTIPLIED_ALPHA");
        }
        return builder.build();
    }

    private static int getShaderIndex(ShaderConfig.ShaderValue shaderValue) {
        if (shaderValue == null) {
            return -1;
        }
        float divideFactor = shaderValue.divideFactor;
        if (Float.compare(divideFactor, PERFECT_DARK_DIVIDE_FACTOR) == 0) {
            return 0;
        }
        if (Float.compare(divideFactor, LESS_PERFECT_DARK_DIVIDE_FACTOR) == 0) {
            return 1;
        }
        if (Float.compare(divideFactor, TOASTED_LIGHT_DIVIDE_FACTOR) == 0) {
            return 2;
        }

        DarkModeEverywhere.LOGGER.warn("Unknown dark mode shader divide factor {}", String.format(Locale.ROOT, "%.2f", divideFactor));
        return -1;
    }
}
