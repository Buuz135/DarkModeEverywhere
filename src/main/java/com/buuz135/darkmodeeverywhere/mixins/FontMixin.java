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
        if (ClientProxy.SELECTED_SHADER != null && Minecraft.getInstance().screen != null) {
            int thre = 65;
            ShaderConfig.ShaderValue shaderValue = ClientProxy.SHADER_VALUES.get(ClientProxy.SELECTED_SHADER);
            if (shaderValue.darkColorReplacement == -1) return color;
            if (FastColor.ARGB32.red(color) < thre && FastColor.ARGB32.green(color)  < thre && FastColor.ARGB32.blue(color)  < thre){
                return shaderValue.darkColorReplacement;
            }
        }
        return color;
    }
}
