package com.buuz135.darkmodeeverywhere;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientProxy {
    public static Object2BooleanMap<String> BLACKLISTED_ELEMENTS = new Object2BooleanOpenHashMap<>();
    public static List<String> MODDED_BLACKLIST = new ArrayList<>();

    public static ShaderConfig CONFIG = new ShaderConfig();
    public static Map<ResourceLocation, ShaderInstance> REGISTERED_SHADERS = new HashMap<>();
    public static List<ResourceLocation> REGISTERED_SHADER_LOCATIONS = new ArrayList<>();
    public static Map<ResourceLocation, ShaderConfig.ShaderValue> SHADER_VALUES = new HashMap<>();
    public static ResourceLocation SELECTED_SHADER = null;

    public ClientProxy(IEventBus modEventBus, ModContainer modContainer) {
        ShaderConfig.load();
        modEventBus.addListener(this::shaderRegister);
        modEventBus.addListener(this::onConfigReload);
        modEventBus.addListener(this::imcCallback);
        NeoForge.EVENT_BUS.addListener(this::openGui);
    }

    @SubscribeEvent
    public void shaderRegister(RegisterShadersEvent event){
        REGISTERED_SHADERS = new HashMap<>();
        REGISTERED_SHADER_LOCATIONS = new ArrayList<>();
        SHADER_VALUES = new HashMap<>();
        for (ShaderConfig.ShaderValue shaderValue : CONFIG.getShaders()) {
            if (SHADER_VALUES.put(shaderValue.resourceLocation, shaderValue) == null) {
                try {
                    event.registerShader(new ShaderInstance(event.getResourceProvider(), shaderValue.resourceLocation, DefaultVertexFormat.POSITION_TEX), shaderInstance -> {
                        DarkModeEverywhere.LOGGER.debug("Registered shader {}", shaderValue.resourceLocation);
                        REGISTERED_SHADERS.put(shaderValue.resourceLocation, shaderInstance);
                        REGISTERED_SHADER_LOCATIONS.add(shaderValue.resourceLocation);
                    });
                } catch (IOException e) {
                    DarkModeEverywhere.LOGGER.trace("Failed to register shader", e);
                }
            }
        }
        if (CONFIG.getSelectedShader() != null){
            SELECTED_SHADER = ResourceLocation.parse(CONFIG.getSelectedShader());
        }
        RenderedClassesTracker.start();
    }

    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading reloading){ BLACKLISTED_ELEMENTS.clear(); }

    private static boolean blacklistContains(List<? extends String> blacklist, String elementName) {
        return blacklist.stream().anyMatch(elementName::contains);
    }

    public static boolean isElementNameBlacklisted(String elementName) {
        return BLACKLISTED_ELEMENTS.computeIfAbsent(elementName, (String name) -> {
            DarkModeEverywhere.LOGGER.debug("Considering {} for element blacklist", name);
            RenderedClassesTracker.add(name);
            return blacklistContains(MODDED_BLACKLIST, name) || blacklistContains(DarkConfig.CLIENT.METHOD_SHADER_BLACKLIST.get(), name);
        });
    }

    @SubscribeEvent
    public void imcCallback(InterModProcessEvent event) {
        event.getIMCStream(string -> string.equals("dme-shaderblacklist")).forEach(imcMessage -> {
            //Validate someone didn't send us something that isn't a string
            if (imcMessage.messageSupplier().get() instanceof String classMethodBlacklist) {
                MODDED_BLACKLIST.add(classMethodBlacklist);
            }
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

    private Tooltip getShaderSwitchButtonTooltip() {
        MutableComponent tooltipComponent = SELECTED_SHADER == null ? Component.translatable("gui." + DarkModeEverywhere.MODID + ".light_mode") : Component.translatable(SHADER_VALUES.get(SELECTED_SHADER).displayName);
        tooltipComponent.append(Component.literal("\n"));
        tooltipComponent.append(Component.translatable("gui.tooltip." + DarkModeEverywhere.MODID + ".shader_switch_tooltip").withStyle(ChatFormatting.GRAY));

        return Tooltip.create(tooltipComponent);
    }

    @SubscribeEvent
    public void openGui(ScreenEvent.Init.Pre event){
       if (event.getScreen() instanceof AbstractContainerScreen || (DarkConfig.CLIENT.SHOW_BUTTON_IN_TITLE_SCREEN.get() && event.getScreen() instanceof TitleScreen)){
           int x = DarkConfig.CLIENT.GUI_BUTTON_X_OFFSET.get();
           int y = DarkConfig.CLIENT.GUI_BUTTON_Y_OFFSET.get();
           if (event.getScreen() instanceof TitleScreen){
               x = DarkConfig.CLIENT.TITLE_SCREEN_BUTTON_X_OFFSET.get();
               y = DarkConfig.CLIENT.TITLE_SCREEN_BUTTON_Y_OFFSET.get();
           }

           Button.Builder buttonBuilder = Button.builder(
               Component.translatable("gui." + DarkModeEverywhere.MODID + ".dark_mode"),
               button -> {
                   SELECTED_SHADER = getNextShaderResourceLocation();
                   CONFIG.setSelectedShader(SELECTED_SHADER);
                   button.setTooltip(getShaderSwitchButtonTooltip());
               });

           buttonBuilder.pos(x, event.getScreen().height - 19 - y);
           buttonBuilder.size(60, 20);

           buttonBuilder.tooltip(getShaderSwitchButtonTooltip());
           Button button = buttonBuilder.build();
           event.addListener(button);
       }
    }
}
