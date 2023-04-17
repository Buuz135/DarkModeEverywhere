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

    @ModifyArg(method = "drawInternal(Ljava/lang/String;FFILorg/joml/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)I"), index = 3)
    public int drawInternalA(int color) {
        return modifyColor(color);
    }


    @ModifyArg(method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFILorg/joml/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"), index = 3)
    public int drawInternal(int color) {
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
