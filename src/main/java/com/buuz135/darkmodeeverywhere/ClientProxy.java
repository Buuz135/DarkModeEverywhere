package com.buuz135.darkmodeeverywhere;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.netty.util.concurrent.*;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
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
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class ClientProxy {
    private final EventExecutor eventExecutor;
    public static Object2BooleanMap<String> BLACKLISTED_ELEMENTS = new Object2BooleanOpenHashMap<>();
    public static List<String> MODDED_BLACKLIST = new ArrayList<>();
    public static ShaderConfig CONFIG = new ShaderConfig();
    public static Map<ShaderConfig.ShaderValue, ShaderInstance> TEX_SHADERS = new HashMap<>();
    public static Map<ShaderConfig.ShaderValue, ShaderInstance> TEX_COLOR_SHADERS = new HashMap<>();
    private static HashMap<ResourceLocation, Promise<ShaderInstance>> ON_SHADERS_LOADED = new HashMap<>();
    public static List<ShaderConfig.ShaderValue> SHADER_VALUES = new ArrayList<>();
    public static ShaderConfig.ShaderValue SELECTED_SHADER_VALUE = null;

    public ClientProxy() {
        eventExecutor = new DefaultEventExecutor();
        ShaderConfig.load();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAllShaders);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcCallback);
        MinecraftForge.EVENT_BUS.addListener(this::openGui);
    }

    private void registerShaderForLoading(RegisterShadersEvent event, ResourceLocation shaderResourceLocation, VertexFormat format) {
        try {
            DarkModeEverywhere.LOGGER.debug("Registering shader {} for loading", shaderResourceLocation);
            ON_SHADERS_LOADED.put(shaderResourceLocation, eventExecutor.newPromise());
            event.registerShader(new DarkShaderInstance(event.getResourceProvider(), shaderResourceLocation, format), (ShaderInstance shaderInstance) -> {
                DarkModeEverywhere.LOGGER.debug("Shader {} has loaded, resolving promise", shaderResourceLocation);
                ON_SHADERS_LOADED.get(shaderResourceLocation).setSuccess(shaderInstance);
            });
        } catch (IOException e) {
            DarkModeEverywhere.LOGGER.trace("Failed to register shader", e);
        }
    }

    public void listenForShaderLoaded(RegisterShadersEvent event, ResourceLocation shaderResourceLocation, VertexFormat format, Consumer<ShaderInstance> onLoaded) {
        if (!(ON_SHADERS_LOADED.containsKey(shaderResourceLocation))) {
            registerShaderForLoading(event, shaderResourceLocation, format);
        }

        Promise<ShaderInstance> onLoadedPromise = ON_SHADERS_LOADED.get(shaderResourceLocation);
        FutureListener<ShaderInstance> listener = (Future<ShaderInstance> shaderInstance) -> onLoaded.accept(shaderInstance.get());
        onLoadedPromise.addListener(listener);
    }

    @SubscribeEvent
    public void registerAllShaders(RegisterShadersEvent event){
        TEX_SHADERS = new HashMap<>();
        TEX_COLOR_SHADERS = new HashMap<>();
        ON_SHADERS_LOADED = new HashMap<>();
        SHADER_VALUES = new ArrayList<>();
        for (ShaderConfig.ShaderValue shaderValue : CONFIG.getShaders()) {
            SHADER_VALUES.add(shaderValue);
            if (shaderValue == null) continue;
            listenForShaderLoaded(event, shaderValue.texShaderLocation, DefaultVertexFormat.POSITION_TEX, (shaderInstance -> {
                TEX_SHADERS.put(shaderValue, shaderInstance);
            }));
            listenForShaderLoaded(event, shaderValue.texColorShaderLocation, DefaultVertexFormat.POSITION_TEX_COLOR, (shaderInstance -> {
                TEX_COLOR_SHADERS.put(shaderValue, shaderInstance);
            }));
        }
        SELECTED_SHADER_VALUE = SHADER_VALUES.get(CONFIG.getSelectedShaderIndex());
        RenderedClassesTracker.start();
    }

    public static ShaderInstance getSelectedTexShader() {
        return TEX_SHADERS.get(SELECTED_SHADER_VALUE);
    }

    public static ShaderInstance getSelectedTexColorShader() {
        return TEX_COLOR_SHADERS.get(SELECTED_SHADER_VALUE);
    }

    public static ShaderConfig.ShaderValue getSelectedShaderValue() {
        return SELECTED_SHADER_VALUE;
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

    private int getNextShaderValueIndex() {
        if (Screen.hasShiftDown()) {
            return 0;
        }

        int nextShaderIndex = SHADER_VALUES.indexOf(SELECTED_SHADER_VALUE) + 1;
        if (nextShaderIndex >= SHADER_VALUES.size()){
            return 0;
        }

        return nextShaderIndex;
    }

    private Tooltip getShaderSwitchButtonTooltip() {
        MutableComponent tooltipComponent = (SELECTED_SHADER_VALUE == null ? Component.translatable("gui." + DarkModeEverywhere.MODID + ".light_mode") : SELECTED_SHADER_VALUE.displayName).plainCopy();
        tooltipComponent.append(Component.literal("\n"));
        tooltipComponent.append(Component.translatable("gui.tooltip." + DarkModeEverywhere.MODID + ".shader_switch_tooltip").withStyle(ChatFormatting.GRAY));

        return Tooltip.create(tooltipComponent);
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

            Button.Builder buttonBuilder = Button.builder(
                    Component.translatable("gui." + DarkModeEverywhere.MODID + ".dark_mode"),
                    button -> {
                        int selectedShaderIndex = getNextShaderValueIndex();
                        CONFIG.setSelectedShaderIndex(selectedShaderIndex);
                        SELECTED_SHADER_VALUE = SHADER_VALUES.get(selectedShaderIndex);
                        button.setTooltip(getShaderSwitchButtonTooltip());
                    });

            buttonBuilder.pos(x, event.getScreen().height - 24 - y);
            buttonBuilder.size(60, 20);

            buttonBuilder.tooltip(getShaderSwitchButtonTooltip());
            Button button = buttonBuilder.build();
            event.addListener(button);
        }
    }
}
