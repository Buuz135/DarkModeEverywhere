package com.buuz135.darkmodeeverywhere;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientProxy {
    public static Object2BooleanMap<String> BLACKLISTED_ELEMENTS = new Object2BooleanOpenHashMap<>();
    public static List<String> MODDED_BLACKLIST = new ArrayList<>();

    public static ShaderConfig CONFIG = new ShaderConfig();
    public static HashMap<ResourceLocation, ShaderInstance> REGISTERED_SHADERS = new HashMap<>();
    public static ArrayList<ResourceLocation> REGISTERED_SHADER_LOCATIONS = new ArrayList<>();
    public static HashMap<ResourceLocation, ShaderConfig.ShaderValue> SHADER_VALUES = new HashMap<>();
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
        REGISTERED_SHADER_LOCATIONS = new ArrayList<>();
        SHADER_VALUES = new HashMap<>();
        List<ResourceLocation> alreadyPendingShaders = new ArrayList<>();
        for (ShaderConfig.ShaderValue shaderValue : CONFIG.getShaders()) {
            SHADER_VALUES.put(shaderValue.resourceLocation, shaderValue);
            if (alreadyPendingShaders.contains(shaderValue.resourceLocation)) continue;
            try {
                event.registerShader(new ShaderInstance(event.getResourceManager(), shaderValue.resourceLocation, DefaultVertexFormat.POSITION_TEX), shaderInstance -> {
                    DarkModeEverywhere.LOGGER.debug("Registered shader " + shaderValue.resourceLocation);
                    REGISTERED_SHADERS.put(shaderValue.resourceLocation, shaderInstance);
                    REGISTERED_SHADER_LOCATIONS.add(shaderValue.resourceLocation);
                });
                alreadyPendingShaders.add(shaderValue.resourceLocation);
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
    public void onConfigReload(ModConfigEvent.Reloading reloading){ BLACKLISTED_ELEMENTS.clear(); }

    private static boolean blacklistContains(List<String> blacklist, String elementName) {
        return blacklist.stream().anyMatch(elementName::contains);
    }

    public static boolean isElementNameBlacklisted(String elementName) {
        return BLACKLISTED_ELEMENTS.computeIfAbsent(elementName, (String name) -> {
            DarkModeEverywhere.LOGGER.debug("Considering " + name + " for element blacklist");
            RenderedClassesTracker.add(name);
            return blacklistContains(MODDED_BLACKLIST, name) || blacklistContains(DarkConfig.CLIENT.METHOD_SHADER_BLACKLIST.get(), name);
        });
    }

    @SubscribeEvent
    public void imcCallback(InterModProcessEvent event) {
        event.getIMCStream(string -> string.equals("dme-shaderblacklist")).forEach(imcMessage -> {
            String classMethodBlacklist = (String) imcMessage.messageSupplier().get();
            MODDED_BLACKLIST.add(classMethodBlacklist);
        });
    }

    private ResourceLocation getNextShaderResourceLocation() {
        if (Screen.hasShiftDown()) {
            return null;
        }

        if (SELECTED_SHADER == null){
            return REGISTERED_SHADER_LOCATIONS.get(0);
        }

        int nextShaderIndex = REGISTERED_SHADER_LOCATIONS.indexOf(SELECTED_SHADER) + 1;
        if (nextShaderIndex >= REGISTERED_SHADERS.size()){
            return null;
        }

        return REGISTERED_SHADER_LOCATIONS.get(nextShaderIndex);
    }

    @SubscribeEvent
    public void openGui(ScreenEvent.Init event){
       if (event.getScreen() instanceof AbstractContainerScreen || (DarkConfig.CLIENT.SHOW_BUTTON_IN_TITLE_SCREEN.get() && event.getScreen() instanceof TitleScreen)){
           int x = DarkConfig.CLIENT.GUI_BUTTON_X_OFFSET.get();
           int y = DarkConfig.CLIENT.GUI_BUTTON_Y_OFFSET.get();
           if (event.getScreen() instanceof TitleScreen){
               x = DarkConfig.CLIENT.TITLE_SCREEN_BUTTON_X_OFFSET.get();
               y = DarkConfig.CLIENT.TITLE_SCREEN_BUTTON_Y_OFFSET.get();
           }

           event.addListener(
               new Button(
                   x, event.getScreen().height - 24 - y, 60, 20,
                   Component.translatable("gui." + DarkModeEverywhere.MODID + ".dark_mode"),
                   button -> {
                       SELECTED_SHADER = getNextShaderResourceLocation();
                       CONFIG.setSelectedShader(SELECTED_SHADER);
                   },
                   (button, poseStack, p_93755_, p_93756_) -> {
                       List<Component> tooltip = new ArrayList<>();
                       tooltip.add(SELECTED_SHADER == null ? Component.translatable("gui." + DarkModeEverywhere.MODID + ".light_mode") : SHADER_VALUES.get(SELECTED_SHADER).displayName);
                       tooltip.add(Component.translatable("gui.tooltip." + DarkModeEverywhere.MODID + ".shader_switch_tooltip").withStyle(ChatFormatting.GRAY));
                       event.getScreen().renderComponentTooltip(poseStack, tooltip, p_93755_, p_93756_);
                   }
               )
           );
       }
    }
}
