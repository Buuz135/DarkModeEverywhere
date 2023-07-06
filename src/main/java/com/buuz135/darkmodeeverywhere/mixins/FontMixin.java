package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.buuz135.darkmodeeverywhere.ShaderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;



@Mixin(Font.class)
public class FontMixin {

    @ModifyArg(method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;adjustColor(I)I"), index = 0)
    public int drawInternalA(int color) {
        return modifyColor(color);
    }

    private int modifyColor(int color){
        if (color == 0) return color;
        if (ClientProxy.SELECTED_SHADER_VALUE != null && Minecraft.getInstance().screen != null) {
            int threshold = 65;
            ShaderConfig.ShaderValue shaderValue = ClientProxy.SELECTED_SHADER_VALUE;
            if (shaderValue.darkColorReplacement == -1) return color;
            if (FastColor.ARGB32.red(color) < threshold && FastColor.ARGB32.green(color) < threshold && FastColor.ARGB32.blue(color) < threshold){
                return shaderValue.darkColorReplacement;
            }
        }
        return color;
    }
}
