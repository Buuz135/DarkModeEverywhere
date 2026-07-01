package com.buuz135.darkmodeeverywhere.mixins;

import com.buuz135.darkmodeeverywhere.ClassUtil;
import com.buuz135.darkmodeeverywhere.ClientProxy;
import com.buuz135.darkmodeeverywhere.ShaderConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
public class FontMixin {

    @Shadow
    @Final
    private int color;

    @Inject(method = "getTextColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void darkModeEverywhere$adjustColorA(TextColor textColor, CallbackInfoReturnable<Integer> cir) {
        int color = textColor == null ? this.color : ARGB.color(ARGB.alpha(this.color), textColor.getValue());
        if (ClientProxy.SELECTED_SHADER_VALUE == null) return;
        if (Minecraft.getInstance().screen == null) return;
        if (color == 0) return;
        var callerClassName = ClassUtil.getCallerClassName();
        if (callerClassName != null && ClientProxy.isElementNameBlacklisted(callerClassName)) return;

        int threshold = 65;
        ShaderConfig.ShaderValue shaderValue = ClientProxy.SELECTED_SHADER_VALUE;
        if (shaderValue.darkColorReplacement == -1) return;
        int rgb = color & 0xFFFFFF;
        if (ChatFormatting.GRAY.getColor().equals(rgb) || ChatFormatting.DARK_GRAY.getColor().equals(rgb)) {
            cir.setReturnValue(withAlpha(color, shaderValue.darkColorReplacement));
            return;
        }
        if (ARGB.red(color) < threshold && ARGB.green(color) < threshold && ARGB.blue(color) < threshold){
            cir.setReturnValue(withAlpha(color, shaderValue.darkColorReplacement));
            return;
        }
    }

    private static int withAlpha(int originalColor, int replacementRgb) {
        int alpha = ARGB.alpha(originalColor);
        return ((alpha == 0 ? 0xFF : alpha) << 24) | (replacementRgb & 0xFFFFFF);
    }

}
