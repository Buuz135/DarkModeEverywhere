package com.buuz135.darkmodeeverywhere;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("darkmodeeverywhere")
public class DarkModeEverywhere {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public DarkModeEverywhere() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
        if (FMLEnvironment.dist == Dist.CLIENT){
            new ClientProxy();
        }
    }

}
