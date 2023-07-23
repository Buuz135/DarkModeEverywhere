package com.buuz135.darkmodeeverywhere;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class DarkConfig {

    public static Client CLIENT = new Client();

    private static abstract class ConfigClass {
        public ForgeConfigSpec SPEC;

        public abstract void onConfigReload(ModConfigEvent.Reloading event);
    }

    public static class Client extends ConfigClass {
        public ForgeConfigSpec.ConfigValue<Integer> GUI_BUTTON_X_OFFSET;
        public ForgeConfigSpec.ConfigValue<Integer> GUI_BUTTON_Y_OFFSET;
        public ForgeConfigSpec.ConfigValue<Integer> TITLE_SCREEN_BUTTON_X_OFFSET;
        public ForgeConfigSpec.ConfigValue<Integer> TITLE_SCREEN_BUTTON_Y_OFFSET;
        public ForgeConfigSpec.ConfigValue<Boolean> SHOW_BUTTON_IN_TITLE_SCREEN;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> METHOD_SHADER_BLACKLIST;
        public ForgeConfigSpec.ConfigValue<Boolean> METHOD_SHADER_DUMP;

        public Client() {
            final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
            List<String> defaultBlacklist = new ArrayList<>(Arrays.asList(
                "mezz.jei.common.render.FluidTankRenderer:drawTextureWithMasking",//1.19.1 JEI Path
                "mezz.jei.library.render.FluidTankRenderer:drawTextureWithMasking",//1.19.2+ JEI Path
                "renderCrosshair", "m_93080_",
                "net.minecraft.client.gui.screens.TitleScreen",
                "renderSky", "m_202423_",
                "renderHotbar", "m_93009_", "m_193837_",//Normal hotbar, and spectator hotbar
                "setupOverlayRenderState",
                "net.minecraftforge.client.gui.overlay.ForgeGui",
                "renderFood",
                "renderExperienceBar", "m_93071_",
                "configured.client.screen.ListMenuScreen", // Configured background
                "OnlineServerEntry:drawIcon", "OnlineServerEntry:m_99889_", // Multiplayer Server icons
                "WorldSelectionList$WorldListEntry:render", "WorldSelectionList$WorldListEntry:m_6311_", // Single player world icons
                "CubeMap:render", "CubeMap:m_108849_", //1.20+ title screen panorama
                "squeek.appleskin.client.HUDOverlayHandler", //AppleSkin overlay
                "shadows.packmenu.ExtendedMenuScreen" //Custom PackMenu backgrounds
            ));

            Predicate<Object> blacklistElementValidator = (Object element) -> true;

            String TRANSLATION_KEY_BASE = "config." + DarkModeEverywhere.MODID + ".";
            METHOD_SHADER_BLACKLIST = BUILDER.comment(
                    "A list of class:method strings (render methods) that the dark shader will not be applied to.",
                    "Each string consists of the class and the method (or any substring) to block the dark shader.",
                    "For example, 'renderHunger' is sufficient to block 'net.minecraftforge.client.gui.overlay.ForgeGui:renderFood' (either will work).")
                .translation(TRANSLATION_KEY_BASE + "method_shader_blacklist")
                .defineList("METHOD_SHADER_BLACKLIST", defaultBlacklist, blacklistElementValidator);
            METHOD_SHADER_DUMP = BUILDER.comment(
                    "Enabling this config will (every 5 seconds) dump which methods were used to render GUIs that the dark shader was applied to",
                    "The dump will consist of a list of class:method strings, e.g. 'net.minecraftforge.client.gui.overlay.ForgeGui:renderFood'",
                    "Use this feature to help find the render method strings of GUIs you would like to blacklist.")
                .translation(TRANSLATION_KEY_BASE + "method_shader_dump")
                .define("METHOD_SHADER_DUMP", false);

            BUILDER.push("Inventory Button");
            GUI_BUTTON_X_OFFSET = BUILDER.comment("Pixels away from the left of the GUI in the x axis")
                .translation(TRANSLATION_KEY_BASE + "button_position_x")
                .defineInRange("X", 32, 0, Integer.MAX_VALUE);
            GUI_BUTTON_Y_OFFSET = BUILDER.comment("Pixels away from the bottom of the GUI in the y axis")
                .translation(TRANSLATION_KEY_BASE + "button_position_y")
                .defineInRange("Y", 2, 0, Integer.MAX_VALUE);
            BUILDER.pop();

            BUILDER.push("Main Menu Button");
            SHOW_BUTTON_IN_TITLE_SCREEN = BUILDER.comment("Enabled")
                .translation(TRANSLATION_KEY_BASE + "enabled")
                .define("SHOW", true);
            TITLE_SCREEN_BUTTON_X_OFFSET = BUILDER.comment("Pixels away from the left of the GUI in the x axis")
                .translation(TRANSLATION_KEY_BASE + "button_position_x")
                .defineInRange("MAIN_X", 4, 0, Integer.MAX_VALUE);
            TITLE_SCREEN_BUTTON_Y_OFFSET = BUILDER.comment("Pixels away from the bottom of the GUI in the y axis")
                .translation(TRANSLATION_KEY_BASE + "button_position_y")
                .defineInRange("MAIN_Y", 40, 0, Integer.MAX_VALUE);
            BUILDER.pop();

            SPEC = BUILDER.build();
        }

        @Override
        public void onConfigReload(ModConfigEvent.Reloading event) {
            if (event.getConfig().getType() == ModConfig.Type.COMMON) {
                SPEC.setConfig(event.getConfig().getConfigData());
            }
        }
    }
}
