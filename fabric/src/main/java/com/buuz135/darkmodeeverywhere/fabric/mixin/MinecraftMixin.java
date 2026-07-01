package com.buuz135.darkmodeeverywhere.fabric.mixin;

import com.buuz135.darkmodeeverywhere.fabric.FabricModResourcePackSource;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>([Lnet/minecraft/server/packs/repository/RepositorySource;)V"
            )
    )
    private RepositorySource[] darkmodeeverywhere$addModResourcePack(RepositorySource[] sources) {
        return FabricModResourcePackSource.appendTo(sources);
    }
}
