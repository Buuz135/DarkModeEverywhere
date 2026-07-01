package com.buuz135.darkmodeeverywhere;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.lwjgl.glfw.GLFW;

public class ClientProxy {
    public static Object2BooleanMap<String> BLACKLISTED_ELEMENTS = new Object2BooleanOpenHashMap<>();
    public static List<String> MODDED_BLACKLIST = new ArrayList<>();
    public static ShaderConfig CONFIG = new ShaderConfig();
    public static List<ShaderConfig.ShaderValue> SHADER_VALUES = new ArrayList<>();
    public static ShaderConfig.ShaderValue SELECTED_SHADER_VALUE = null;

    public ClientProxy() {
        DarkModeEverywhere.init();
    }

    public void registerAllShaders(){
        SHADER_VALUES = new ArrayList<>();
        for (ShaderConfig.ShaderValue shaderValue : CONFIG.getShaders()) {
            SHADER_VALUES.add(shaderValue);
        }
        SELECTED_SHADER_VALUE = SHADER_VALUES.get(CONFIG.getSelectedShaderIndex());
        RenderedClassesTracker.start();
    }

    public static ShaderConfig.ShaderValue getSelectedShaderValue() {
        return SELECTED_SHADER_VALUE;
    }

    public void onConfigReload() {
        BLACKLISTED_ELEMENTS.clear();
    }

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

    public void addShaderBlacklist(String classMethodBlacklist) {
        MODDED_BLACKLIST.add(classMethodBlacklist);
    }

    private int getNextShaderValueIndex() {
        if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) {
            return 0;
        }

        int nextShaderIndex = SHADER_VALUES.indexOf(SELECTED_SHADER_VALUE) + 1;
        if (nextShaderIndex >= SHADER_VALUES.size()){
            return 0;
        }

        return nextShaderIndex;
    }

    private Tooltip getShaderSwitchButtonTooltip() {
        MutableComponent tooltipComponent = (SELECTED_SHADER_VALUE == null ? Component.translatable("gui." + DarkModeEverywhere.MODID + ".light_mode") : SELECTED_SHADER_VALUE.getDisplayName()).plainCopy();
        tooltipComponent.append(Component.literal("\n"));
        tooltipComponent.append(Component.translatable("gui.tooltip." + DarkModeEverywhere.MODID + ".shader_switch_tooltip").withStyle(ChatFormatting.GRAY));

        return Tooltip.create(tooltipComponent);
    }

    public void openGui(Screen screen, Consumer<Button> addButton){
       if ((screen instanceof AbstractContainerScreen && DarkConfig.CLIENT.SHOW_BUTTON_IN_INVENTORY.get()) || (screen instanceof TitleScreen && DarkConfig.CLIENT.SHOW_BUTTON_IN_TITLE_SCREEN.get())){
           int x = DarkConfig.CLIENT.GUI_BUTTON_X_OFFSET.get();
           int y = DarkConfig.CLIENT.GUI_BUTTON_Y_OFFSET.get();
           if (screen instanceof TitleScreen){
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

           buttonBuilder.pos(x, screen.height - 19 - y);
           buttonBuilder.size(60, 20);

           buttonBuilder.tooltip(getShaderSwitchButtonTooltip());
           addButton.accept(buttonBuilder.build());
       }
    }
}
