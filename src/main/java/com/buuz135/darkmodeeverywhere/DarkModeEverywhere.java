package com.buuz135.darkmodeeverywhere;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("darkmodeeverywhere")
public class DarkModeEverywhere {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public DarkModeEverywhere() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
        DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> ClientProxy::new);
    }

}
