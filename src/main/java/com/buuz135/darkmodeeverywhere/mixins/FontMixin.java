package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;



@Mixin(Font.class)
public class FontMixin {

    @ModifyArg(method = "drawInternal(Ljava/lang/String;FFILcom/mojang/math/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZIIZ)I"), index = 3)
    public int drawInternalA(int color) {
        return darkModeEverywhere$modifyColor(color);
    }

    @ModifyArg(method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFILcom/mojang/math/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"), index = 3)
    public int drawInternal(int color) {
        return darkModeEverywhere$modifyColor(color);
    }

    @Unique
    private int darkModeEverywhere$modifyColor(int color){
        if (color == 0) return color;
        if (ClientProxy.SELECTED_SHADER_VALUE != null && Minecraft.getInstance().screen != null) {
            int threshold = 65;
            if (ClientProxy.SELECTED_SHADER_VALUE.darkColorReplacement == -1) return color;
            if (FastColor.ARGB32.red(color) < threshold && FastColor.ARGB32.green(color) < threshold && FastColor.ARGB32.blue(color) < threshold){
                return ClientProxy.SELECTED_SHADER_VALUE.darkColorReplacement;
            }
        }
        return color;
    }
}
