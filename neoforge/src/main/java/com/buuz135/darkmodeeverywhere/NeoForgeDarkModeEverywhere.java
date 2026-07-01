package com.buuz135.darkmodeeverywhere;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(DarkModeEverywhere.MODID)
public class NeoForgeDarkModeEverywhere {

    private ClientProxy clientProxy;

    public NeoForgeDarkModeEverywhere(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeDarkConfig neoForgeDarkConfig = new NeoForgeDarkConfig();
        modContainer.registerConfig(ModConfig.Type.CLIENT, neoForgeDarkConfig.getSpec());

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            clientProxy = new ClientProxy();
            clientProxy.registerAllShaders();
            modEventBus.addListener(this::onConfigReload);
            modEventBus.addListener(this::imcCallback);
            NeoForge.EVENT_BUS.addListener(this::openGui);
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading reloading) {
        clientProxy.onConfigReload();
    }

    private void imcCallback(InterModProcessEvent event) {
        event.getIMCStream(string -> string.equals("dme-shaderblacklist")).forEach(imcMessage -> {
            if (imcMessage.messageSupplier().get() instanceof String classMethodBlacklist) {
                clientProxy.addShaderBlacklist(classMethodBlacklist);
            }
        });
    }

    private void openGui(ScreenEvent.Init.Pre event) {
        clientProxy.openGui(event.getScreen(), event::addListener);
    }
}
