package com.buuz135.darkmodeeverywhere;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class NeoForgeDarkConfig {

    private final ModConfigSpec spec;

    public NeoForgeDarkConfig() {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String translationKeyBase = "config." + DarkModeEverywhere.MODID + ".";

        ModConfigSpec.ConfigValue<List<? extends String>> methodShaderBlacklist = builder.comment(
                        "A list of class:method strings (render methods) that the dark shader will not be applied to.",
                        "Each string consists of the class and the method (or any substring) to block the dark shader.",
                        "For example, 'renderHunger' is sufficient to block 'net.minecraftforge.client.gui.overlay.ForgeGui:renderFood' (either will work).")
                .translation(translationKeyBase + "method_shader_blacklist")
                .defineList("METHOD_SHADER_BLACKLIST", DarkConfig.defaultBlacklist(), () -> "", o -> true);
        ModConfigSpec.ConfigValue<Boolean> methodShaderDump = builder.comment(
                        "Enabling this config will (every 5 seconds) dump which methods were used to render GUIs that the dark shader was applied to",
                        "The dump will consist of a list of class:method strings, e.g. 'net.minecraftforge.client.gui.overlay.ForgeGui:renderFood'",
                        "Use this feature to help find the render method strings of GUIs you would like to blacklist.")
                .translation(translationKeyBase + "method_shader_dump")
                .define("METHOD_SHADER_DUMP", false);

        builder.push("Inventory Button");
        ModConfigSpec.ConfigValue<Integer> guiButtonXOffset = builder.comment("Pixels away from the left of the GUI in the x axis")
                .translation(translationKeyBase + "button_position_x")
                .defineInRange("X", 32, 0, Integer.MAX_VALUE);
        ModConfigSpec.ConfigValue<Integer> guiButtonYOffset = builder.comment("Pixels away from the bottom of the GUI in the y axis")
                .translation(translationKeyBase + "button_position_y")
                .defineInRange("Y", 2, 0, Integer.MAX_VALUE);
        ModConfigSpec.ConfigValue<Boolean> showButtonInInventory = builder.comment("Enabled")
                .translation(translationKeyBase + "enabled")
                .define("SHOW", true);
        builder.pop();

        builder.push("Main Menu Button");
        ModConfigSpec.ConfigValue<Boolean> showButtonInTitleScreen = builder.comment("Enabled")
                .translation(translationKeyBase + "enabled")
                .define("SHOW", true);
        ModConfigSpec.ConfigValue<Integer> titleScreenButtonXOffset = builder.comment("Pixels away from the left of the GUI in the x axis")
                .translation(translationKeyBase + "button_position_x")
                .defineInRange("MAIN_X", 4, 0, Integer.MAX_VALUE);
        ModConfigSpec.ConfigValue<Integer> titleScreenButtonYOffset = builder.comment("Pixels away from the bottom of the GUI in the y axis")
                .translation(translationKeyBase + "button_position_y")
                .defineInRange("MAIN_Y", 40, 0, Integer.MAX_VALUE);
        builder.pop();

        spec = builder.build();
        DarkConfig.install(new DarkConfig.Client(
                guiButtonXOffset::get,
                guiButtonYOffset::get,
                showButtonInInventory::get,
                titleScreenButtonXOffset::get,
                titleScreenButtonYOffset::get,
                showButtonInTitleScreen::get,
                methodShaderBlacklist::get,
                methodShaderDump::get
        ));
    }

    public ModConfigSpec getSpec() {
        return spec;
    }
}
