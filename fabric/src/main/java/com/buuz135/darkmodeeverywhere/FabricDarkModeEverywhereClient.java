package com.buuz135.darkmodeeverywhere;

import net.fabricmc.api.ClientModInitializer;

public class FabricDarkModeEverywhereClient implements ClientModInitializer {

    public static ClientProxy clientProxy;

    @Override
    public void onInitializeClient() {
        DarkConfig.loadStandalone();
        clientProxy = new ClientProxy();
        clientProxy.registerAllShaders();
    }
}
