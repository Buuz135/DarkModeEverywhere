package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.buuz135.darkmodeeverywhere.ShaderConfig;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.awt.*;


@Mixin(Font.class)
public class FontMixin {

    @ModifyArg(method = "drawInternal(Ljava/lang/String;FFILcom/mojang/math/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZIIZ)I"), index = 3)
    public int drawInternalA(int color) {
        return modifyColor(color);
    }


    @ModifyArg(method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFILcom/mojang/math/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"), index = 3)
    public int drawInternal(int color) {
        return modifyColor(color);
    }

    private int modifyColor(int color){
        if (ClientProxy.SELECTED_SHADER != null) {
            int thre = 65;
            ShaderConfig.Value value = ClientProxy.SHADER_VALUES.get(ClientProxy.SELECTED_SHADER);
            if (value.darkColorReplacement == -1) return color;
            Color color1 = new Color(color);
            if (color1.getRed() < thre && color1.getGreen() < thre && color1.getBlue() < thre){
                return value.darkColorReplacement;
            }
        }
        return color;
    }
}
