package com.buuz135.darkmodeeverywhere;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod("darkmodeeverywhere")
public class DarkModeEverywhere {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "darkmodeeverywhere";

    public DarkModeEverywhere(IEventBus modEventBus, ModContainer modContainer) {
        //modContainer.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
        if (FMLEnvironment.dist == Dist.CLIENT){
            new ClientProxy(modEventBus, modContainer);
        }
        modContainer.registerConfig(ModConfig.Type.STARTUP, DarkConfig.CLIENT.SPEC);
        //modEventBus.addListener(DarkConfig.CLIENT::onConfigReload);
    }

}
