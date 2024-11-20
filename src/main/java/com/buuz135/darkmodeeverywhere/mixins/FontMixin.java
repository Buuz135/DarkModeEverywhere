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

    @Inject(method = "adjustColor", at = @At(value = "HEAD", target = "Lnet/minecraft/client/gui/Font;adjustColor(I)I"), cancellable = true)
    private static void adjustColorA(int color, CallbackInfoReturnable<Integer> cir) {
        if (ClientProxy.SELECTED_SHADER != null && Minecraft.getInstance().screen != null) {
            if (color == 0) return;
            int thre = 65;
            ShaderConfig.ShaderValue shaderValue = ClientProxy.SHADER_VALUES.get(ClientProxy.SELECTED_SHADER);
            if (shaderValue.darkColorReplacement == -1) return;
            if (ChatFormatting.GRAY.getColor().equals(color) || ChatFormatting.DARK_GRAY.getColor().equals(color)) {
                cir.setReturnValue(0xFF000000 | shaderValue.darkColorReplacement);
                return;
            }
            if (FastColor.ARGB32.red(color) < thre && FastColor.ARGB32.green(color)  < thre && FastColor.ARGB32.blue(color)  < thre){
                cir.setReturnValue(0xFF000000 | shaderValue.darkColorReplacement);
                return;
            }
        }
    }

}
