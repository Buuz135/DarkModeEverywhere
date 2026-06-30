package com.buuz135.darkmodeeverywhere;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.buuz135.darkmodeeverywhere.fabric.mixin.ScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.function.Consumer;

public class FabricDarkModeEverywhereClient implements ClientModInitializer {

    private ClientProxy clientProxy;

    @Override
    public void onInitializeClient() {
        DarkConfig.loadStandalone();
        clientProxy = new ClientProxy();

        CoreShaderRegistrationCallback.EVENT.register(context ->
                clientProxy.registerAllShaders((ResourceLocation shaderResourceLocation, VertexFormat format, Consumer<ShaderInstance> onLoaded) -> registerShader(context, shaderResourceLocation, format, onLoaded))
        );

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                clientProxy.openGui(screen, button -> ((ScreenAccessor) screen).darkmodeeverywhere$addRenderableWidget(button))
        );
    }

    private void registerShader(CoreShaderRegistrationCallback.RegistrationContext context, ResourceLocation shaderResourceLocation, VertexFormat format, Consumer<ShaderInstance> onLoaded) throws IOException {
        context.register(shaderResourceLocation, format, onLoaded);
    }
}
