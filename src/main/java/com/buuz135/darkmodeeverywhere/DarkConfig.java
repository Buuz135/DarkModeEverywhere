package com.buuz135.darkmodeeverywhere;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DarkConfig {

    public static Client CLIENT = new Client();

    private static abstract class ConfigClass {
        public ForgeConfigSpec SPEC;

        public abstract void onConfigReload(ModConfigEvent.Reloading event);
    }

    public static class Client extends ConfigClass {
        public ForgeConfigSpec.ConfigValue<Integer> X;
        public ForgeConfigSpec.ConfigValue<Integer> Y;
        public ForgeConfigSpec.ConfigValue<String> NAME;

        public ForgeConfigSpec.ConfigValue<Integer> MAIN_X;
        public ForgeConfigSpec.ConfigValue<Integer> MAIN_Y;
        public ForgeConfigSpec.ConfigValue<Boolean> SHOW_IN_MAIN;
        public ForgeConfigSpec.ConfigValue<String> MAIN_NAME;
        public ForgeConfigSpec.ConfigValue<List<String>> METHOD_SHADER_BLACKLIST;
        public ForgeConfigSpec.ConfigValue<Boolean> METHOD_SHADER_DUMP;

        public Client() {
            final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
            List<String> defaultBlacklist = new ArrayList<>();
            defaultBlacklist.addAll(Arrays.asList("mezz.jei.common.render.FluidTankRenderer:drawTextureWithMasking","renderCrosshair", "net.minecraft.client.gui.screens.TitleScreen","renderSky", "renderHotbar", "setupOverlayRenderState", "net.minecraftforge.client.gui.overlay.ForgeGui", "renderFood", "renderExperienceBar"));
            METHOD_SHADER_BLACKLIST = BUILDER.comment(
                            "A list of strings that will prevent the dark shader to be used.",
                            "Each string consists of the class and the method or any part of it to prevent the use of the dark shader.",
                            "The dump will consist of a bunch of string with this style 'net.minecraftforge.client.gui.overlay.ForgeGui:renderHunger' (class:method), any part of that dump (like just using renderHunger) will prevent the hunger bar from using the dark shader.")
                    .define("METHOD_SHADER_BLACKLIST", defaultBlacklist);
            METHOD_SHADER_DUMP = BUILDER.comment("Enabling this config will dump every 5 seconds which classes & methods were used to render something in the screen and the dark shader was trying to be used").define("METHOD_SHADER_DUMP", false);
            BUILDER.push("Button Position");
            X = BUILDER.comment("Pixels away from the bottom left of the GUI in the x axis").defineInRange("X", 4, 0, Integer.MAX_VALUE);
            Y = BUILDER.comment("Pixels away from the bottom left of the GUI in the y axis").defineInRange("Y", 0, 0, Integer.MAX_VALUE);
            NAME = BUILDER.define("NAME", "Dark Mode");
            BUILDER.pop();
            BUILDER.push("Main Menu Button");
            SHOW_IN_MAIN = BUILDER.comment("Enabled").define("SHOW", true);
            MAIN_X = BUILDER.comment("Pixels away from the bottom left of the GUI in the x axis").defineInRange("X", 4, 0, Integer.MAX_VALUE);
            MAIN_Y = BUILDER.comment("Pixels away from the bottom left of the GUI in the y axis").defineInRange("Y", 40, 0, Integer.MAX_VALUE);
            MAIN_NAME = BUILDER.define("NAME", "Dark Mode");
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
