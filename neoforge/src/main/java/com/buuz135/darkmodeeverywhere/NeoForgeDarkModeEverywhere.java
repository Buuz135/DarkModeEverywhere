package com.buuz135.darkmodeeverywhere;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;
import java.util.function.Consumer;

@Mod(DarkModeEverywhere.MODID)
public class NeoForgeDarkModeEverywhere {

    private ClientProxy clientProxy;

    public NeoForgeDarkModeEverywhere(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeDarkConfig neoForgeDarkConfig = new NeoForgeDarkConfig();
        modContainer.registerConfig(ModConfig.Type.CLIENT, neoForgeDarkConfig.getSpec());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            clientProxy = new ClientProxy();
            modEventBus.addListener(this::registerAllShaders);
            modEventBus.addListener(this::onConfigReload);
            modEventBus.addListener(this::imcCallback);
            NeoForge.EVENT_BUS.addListener(this::openGui);
        }
    }

    private void registerAllShaders(RegisterShadersEvent event) {
        clientProxy.registerAllShaders((ResourceLocation shaderResourceLocation, VertexFormat format, Consumer<ShaderInstance> onLoaded) -> registerShader(event, shaderResourceLocation, format, onLoaded));
    }

    private void registerShader(RegisterShadersEvent event, ResourceLocation shaderResourceLocation, VertexFormat format, Consumer<ShaderInstance> onLoaded) throws IOException {
        event.registerShader(new DarkShaderInstance(event.getResourceProvider(), shaderResourceLocation, format), onLoaded);
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
