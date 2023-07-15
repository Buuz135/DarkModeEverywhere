package com.buuz135.darkmodeeverywhere;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod("darkmodeeverywhere")
public class DarkModeEverywhere {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "darkmodeeverywhere";

    public DarkModeEverywhere() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
        if (FMLEnvironment.dist == Dist.CLIENT){
            new ClientProxy();
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DarkConfig.CLIENT.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DarkConfig.CLIENT::onConfigReload);
    }

}
