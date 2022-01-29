package com.buuz135.darkmodeeverywhere;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientProxy {

    public static ShaderConfig CONFIG = new ShaderConfig();
    public static HashMap<ResourceLocation, ShaderInstance> REGISTERED_SHADERS = new HashMap<>();
    public static HashMap<ResourceLocation, ShaderConfig.Value> SHADER_VALUES = new HashMap<>();
    public static ResourceLocation SELECTED_SHADER = null;

    public ClientProxy() {
        ShaderConfig.load();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::shaderRegister);
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
    }

    @SubscribeEvent
    public void openGui(ScreenEvent.InitScreenEvent event){
       if (Minecraft.getInstance().level != null && (event.getScreen() instanceof AbstractContainerScreen)){
           event.addListener(new Button(60, event.getScreen().height - 24, 60, 20, new TextComponent("Dark Mode"), but -> {
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
               tooltip.add(SELECTED_SHADER == null ? new TextComponent("Light Mode") : new TextComponent(SHADER_VALUES.get(SELECTED_SHADER).displayName));
               tooltip.add(new TextComponent(" * Use shift to change it to Light Mode").withStyle(ChatFormatting.GRAY));
               event.getScreen().renderComponentTooltip(p_93754_,tooltip,  p_93755_, p_93756_);
           }));
       }
    }
}
