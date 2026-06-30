package com.buuz135.darkmodeeverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DarkConfig {

    public static Client CLIENT = Client.defaults();

    private static final File CONFIG_FILE = new File("config" + File.separator + DarkModeEverywhere.MODID + ".json");

    public static void install(Client client) {
        CLIENT = client;
    }

    public static void loadStandalone() {
        if (!CONFIG_FILE.exists()) {
            saveStandalone();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            StoredConfig storedConfig = new Gson().fromJson(reader, StoredConfig.class);
            CLIENT = storedConfig.toClient();
        } catch (Exception exception) {
            DarkModeEverywhere.LOGGER.warn("Failed to load {}, using defaults", CONFIG_FILE, exception);
            CLIENT = Client.defaults();
            saveStandalone();
        }
    }

    private static void saveStandalone() {
        CONFIG_FILE.getParentFile().mkdirs();
        StoredConfig storedConfig = StoredConfig.from(CLIENT);
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(storedConfig, writer);
        } catch (IOException exception) {
            DarkModeEverywhere.LOGGER.warn("Failed to save {}", CONFIG_FILE, exception);
        }
    }

    public interface Value<T> {
        T get();
    }

    public record SimpleValue<T>(T value) implements Value<T> {
        @Override
        public T get() {
            return value;
        }
    }

    public static class Client {
        public Value<Integer> GUI_BUTTON_X_OFFSET;
        public Value<Integer> GUI_BUTTON_Y_OFFSET;
        public Value<Boolean> SHOW_BUTTON_IN_INVENTORY;
        public Value<Integer> TITLE_SCREEN_BUTTON_X_OFFSET;
        public Value<Integer> TITLE_SCREEN_BUTTON_Y_OFFSET;
        public Value<Boolean> SHOW_BUTTON_IN_TITLE_SCREEN;
        public Value<List<? extends String>> METHOD_SHADER_BLACKLIST;
        public Value<Boolean> METHOD_SHADER_DUMP;

        public Client(
                Value<Integer> guiButtonXOffset,
                Value<Integer> guiButtonYOffset,
                Value<Boolean> showButtonInInventory,
                Value<Integer> titleScreenButtonXOffset,
                Value<Integer> titleScreenButtonYOffset,
                Value<Boolean> showButtonInTitleScreen,
                Value<List<? extends String>> methodShaderBlacklist,
                Value<Boolean> methodShaderDump
        ) {
            GUI_BUTTON_X_OFFSET = guiButtonXOffset;
            GUI_BUTTON_Y_OFFSET = guiButtonYOffset;
            SHOW_BUTTON_IN_INVENTORY = showButtonInInventory;
            TITLE_SCREEN_BUTTON_X_OFFSET = titleScreenButtonXOffset;
            TITLE_SCREEN_BUTTON_Y_OFFSET = titleScreenButtonYOffset;
            SHOW_BUTTON_IN_TITLE_SCREEN = showButtonInTitleScreen;
            METHOD_SHADER_BLACKLIST = methodShaderBlacklist;
            METHOD_SHADER_DUMP = methodShaderDump;
        }

        public static Client defaults() {
            return new Client(
                    new SimpleValue<>(32),
                    new SimpleValue<>(2),
                    new SimpleValue<>(true),
                    new SimpleValue<>(4),
                    new SimpleValue<>(40),
                    new SimpleValue<>(true),
                    new SimpleValue<>(defaultBlacklist()),
                    new SimpleValue<>(false)
            );
        }
    }

    public static List<String> defaultBlacklist() {
        return new ArrayList<>(Arrays.asList(
                "mezz.jei.common.render.FluidTankRenderer:drawTextureWithMasking",
                "mezz.jei.library.render.FluidTankRenderer:drawTextureWithMasking",
                "renderCrosshair", "m_93080_",
                "renderSky", "m_202423_",
                "renderHotbar", "m_93009_", "m_193837_",
                "setupOverlayRenderState",
                "net.minecraftforge.client.gui.overlay.ForgeGui",
                "renderFood",
                "renderExperienceBar", "m_93071_",
                "renderLogo", "m_280037_", "m_280118_",
                "net.minecraft.client.gui.Gui", "net.minecraft.src.C_3431_",
                "renderDirtBackground", "m_280039_", "m_280039_",
                "configured.client.screen.ListMenuScreen",
                "OnlineServerEntry:drawIcon", "OnlineServerEntry:m_99889_",
                "WorldSelectionList$WorldListEntry:render", "WorldSelectionList$WorldListEntry:m_6311_",
                "CubeMap:render", "CubeMap:m_108849_",
                "squeek.appleskin.client.HUDOverlayHandler",
                "shadows.packmenu.ExtendedMenuScreen",
                "dzwdz.chat_heads.ChatHeads:renderChatHead"
        ));
    }

    private static class StoredConfig {
        int guiButtonXOffset = 32;
        int guiButtonYOffset = 2;
        boolean showButtonInInventory = true;
        int titleScreenButtonXOffset = 4;
        int titleScreenButtonYOffset = 40;
        boolean showButtonInTitleScreen = true;
        List<String> methodShaderBlacklist = defaultBlacklist();
        boolean methodShaderDump = false;

        static StoredConfig from(Client client) {
            StoredConfig storedConfig = new StoredConfig();
            storedConfig.guiButtonXOffset = client.GUI_BUTTON_X_OFFSET.get();
            storedConfig.guiButtonYOffset = client.GUI_BUTTON_Y_OFFSET.get();
            storedConfig.showButtonInInventory = client.SHOW_BUTTON_IN_INVENTORY.get();
            storedConfig.titleScreenButtonXOffset = client.TITLE_SCREEN_BUTTON_X_OFFSET.get();
            storedConfig.titleScreenButtonYOffset = client.TITLE_SCREEN_BUTTON_Y_OFFSET.get();
            storedConfig.showButtonInTitleScreen = client.SHOW_BUTTON_IN_TITLE_SCREEN.get();
            storedConfig.methodShaderBlacklist = new ArrayList<>(client.METHOD_SHADER_BLACKLIST.get());
            storedConfig.methodShaderDump = client.METHOD_SHADER_DUMP.get();
            return storedConfig;
        }

        Client toClient() {
            if (methodShaderBlacklist == null) {
                methodShaderBlacklist = defaultBlacklist();
            }
            return new Client(
                    new SimpleValue<>(guiButtonXOffset),
                    new SimpleValue<>(guiButtonYOffset),
                    new SimpleValue<>(showButtonInInventory),
                    new SimpleValue<>(titleScreenButtonXOffset),
                    new SimpleValue<>(titleScreenButtonYOffset),
                    new SimpleValue<>(showButtonInTitleScreen),
                    new SimpleValue<>(methodShaderBlacklist),
                    new SimpleValue<>(methodShaderDump)
            );
        }
    }
}
