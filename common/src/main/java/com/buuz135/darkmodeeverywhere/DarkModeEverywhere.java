package com.buuz135.darkmodeeverywhere;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class DarkModeEverywhere {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "darkmodeeverywhere";

    public static void init() {
        ShaderConfig.load();
    }
}
