package com.buuz135.darkmodeeverywhere;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientProxy {

    public static List<String> BLACKLISTED_ELEMENTS = new ArrayList<>();
    public static List<String> MODDED_BLACKLIST = new ArrayList<>();

    public static ShaderConfig CONFIG = new ShaderConfig();
    public static HashMap<ResourceLocation, ShaderInstance> REGISTERED_SHADERS = new HashMap<>();
    public static HashMap<ResourceLocation, ShaderConfig.Value> SHADER_VALUES = new HashMap<>();
    public static ResourceLocation SELECTED_SHADER = null;

    public ClientProxy() {
        ShaderConfig.load();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::shaderRegister);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcCallback);
        MinecraftForge.EVENT_BUS.addListener(this::openGui);
    }

    @SubscribeEvent
    public void shaderRegister(RegisterShadersEvent event){
        REGISTERED_SHADERS = new HashMap<>();
        SHADER_VALUES = new HashMap<>();
        List<String> loaderShaders = new ArrayList<>();
        for (ShaderConfig.Value shader : CONFIG.getShaders()) {
            SHADER_VALUES.put(new ResourceLocation(shader.resourceLocation), shader);
            if (loaderShaders.contains(shader.resourceLocation)) continue;
            try {
                event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(shader.resourceLocation), DefaultVertexFormat.POSITION_TEX),  shaderInstance -> {
                    REGISTERED_SHADERS.put(new ResourceLocation(shader.resourceLocation), shaderInstance);
                });
                DarkModeEverywhere.LOGGER.info("Registered shader " + shader.resourceLocation);
                loaderShaders.add(shader.resourceLocation);
            } catch (IOException e) {
                DarkModeEverywhere.LOGGER.trace(e);
            }
        }
        if (CONFIG.getSelectedShader() != null){
            SELECTED_SHADER = new ResourceLocation(CONFIG.getSelectedShader());
        }
        RenderedClassesTracker.start();
    }

    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading reloading){
        BLACKLISTED_ELEMENTS.clear();
    }

    @SubscribeEvent
    public void imcCallback(InterModProcessEvent event) {
        event.getIMCStream(string -> string.equals("dme-shaderblacklist")).forEach(imcMessage -> {
            String classMethodBlacklist = (String) imcMessage.messageSupplier().get();
            MODDED_BLACKLIST.add(classMethodBlacklist);
        });

    }

    @SubscribeEvent
    public void openGui(ScreenEvent.Init event){
       if (event.getScreen() instanceof AbstractContainerScreen || (DarkConfig.CLIENT.SHOW_IN_MAIN.get() && event.getScreen() instanceof TitleScreen)){
           int x = DarkConfig.CLIENT.X.get();
           int y = DarkConfig.CLIENT.Y.get();
           if (event.getScreen() instanceof TitleScreen){
               x = DarkConfig.CLIENT.MAIN_X.get();
               y = DarkConfig.CLIENT.MAIN_Y.get();
           }
           event.addListener(new Button(x, event.getScreen().height - 24 - y, 60, 20, Component.literal(event.getScreen() instanceof TitleScreen ? DarkConfig.CLIENT.MAIN_NAME.get() : DarkConfig.CLIENT.NAME.get()), but -> {
               if (Screen.hasShiftDown()){
                   SELECTED_SHADER = null;

               }else if (SELECTED_SHADER == null){
                   SELECTED_SHADER = (ResourceLocation) REGISTERED_SHADERS.keySet().toArray()[0];
               } else {
                   int nextShader = new ArrayList<>(REGISTERED_SHADERS.keySet()).indexOf(SELECTED_SHADER) + 1;
                   if (nextShader > REGISTERED_SHADERS.size() - 1){
                       SELECTED_SHADER = null;
                   }else {
                       SELECTED_SHADER = new ArrayList<>(REGISTERED_SHADERS.keySet()).get(nextShader);
                   }
               }
               CONFIG.setSelectedShader(SELECTED_SHADER);
           }, (p_93753_, p_93754_, p_93755_, p_93756_) -> {
               List<Component> tooltip = new ArrayList<>();
               tooltip.add(SELECTED_SHADER == null ? Component.literal("Light Mode") : Component.literal(SHADER_VALUES.get(SELECTED_SHADER).displayName));
               tooltip.add(Component.literal(" * Use shift to change it to Light Mode").withStyle(ChatFormatting.GRAY));
               event.getScreen().renderComponentTooltip(p_93754_,tooltip,  p_93755_, p_93756_);
           }));
       }
    }
}
