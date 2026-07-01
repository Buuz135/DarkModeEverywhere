package com.buuz135.darkmodeeverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ShaderConfig {

    private List<ShaderValue> shaders;
    private int version;
    private int selectedShaderIndex;
    private static final File configFilePath = new File("config" + File.separator + "darkmodeeverywhereshaders.json");

    public ShaderConfig() {
        this.shaders = new ArrayList<>();
        this.version = 2;
        Identifier tex_shader_location = Identifier.fromNamespaceAndPath("darkmodeeverywhere", "dark_position_tex");
        Identifier tex_color_shader_location = Identifier.fromNamespaceAndPath("darkmodeeverywhere", "dark_position_tex_color");
        this.shaders.add(null);
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, "gui.darkmodeeverywhere.perfect_dark", (float)5.5, 16777215));
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, "gui.darkmodeeverywhere.less_perfect_dark", (float)3.5, 16777215));
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, "gui.darkmodeeverywhere.toasted_light", (float)2, 16777215));
        this.selectedShaderIndex = 0;
    }
 
    public List<ShaderValue> getShaders() {
        return shaders;
    }

    public void setSelectedShaderIndex(int index) {
        selectedShaderIndex = index;
        DarkModeEverywhere.LOGGER.debug("Selected shader index updated to {}", selectedShaderIndex);
        new Thread(ShaderConfig::createDefaultConfigFile).start();
    }

    public int getSelectedShaderIndex() {
        return selectedShaderIndex;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public static void load(){
        if (!configFilePath.exists()){
            createDefaultConfigFile();
        }
        Gson gson = createGson();
        try (FileReader reader = new FileReader(configFilePath)) {
            ClientProxy.CONFIG = gson.fromJson(reader, ShaderConfig.class);
            if (ClientProxy.CONFIG == null || ClientProxy.CONFIG.version != new ShaderConfig().version) {
                throw new Exception("Invalid config version.");
            }
            ClientProxy.CONFIG.sanitize();
        } catch (Exception e) {
            e.printStackTrace();
            ClientProxy.CONFIG = new ShaderConfig();
            createDefaultConfigFile();
        }
    }

    private static void createDefaultConfigFile(){
        Gson gson = createGson();
        try (FileWriter fileWriter = new FileWriter(ShaderConfig.configFilePath)) {
            gson.toJson(ClientProxy.CONFIG, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sanitize() {
        ShaderConfig defaultConfig = new ShaderConfig();
        boolean changed = false;
        if (shaders == null || shaders.size() != defaultConfig.shaders.size() || hasInvalidShaderEntries(defaultConfig.shaders)) {
            shaders = defaultConfig.shaders;
            changed = true;
        }
        if (selectedShaderIndex < 0 || selectedShaderIndex >= shaders.size()) {
            selectedShaderIndex = defaultConfig.selectedShaderIndex;
            changed = true;
        }
        if (changed) {
            createDefaultConfigFile();
        }
    }

    private boolean hasInvalidShaderEntries(List<ShaderValue> defaultShaders) {
        for (int i = 0; i < shaders.size(); i++) {
            ShaderValue shaderValue = shaders.get(i);
            ShaderValue defaultShaderValue = defaultShaders.get(i);
            if (defaultShaderValue == null) {
                if (shaderValue != null) {
                    return true;
                }
                continue;
            }
            if (shaderValue == null || shaderValue.displayTranslationKey == null || shaderValue.displayTranslationKey.isBlank()) {
                return true;
            }
        }
        return false;
    }

    public static class ShaderValue {
        public Identifier texShaderLocation;
        public Identifier texColorShaderLocation;
        public String displayTranslationKey;
        public float divideFactor;
        public int darkColorReplacement;

        public ShaderValue(Identifier texShaderLocation, Identifier texColorShaderLocation, String displayTranslationKey, float divideFactor, int darkColorReplacement) {
            this.texShaderLocation = texShaderLocation;
            this.texColorShaderLocation = texColorShaderLocation;
            this.displayTranslationKey = displayTranslationKey;
            this.divideFactor = divideFactor;
            this.darkColorReplacement = darkColorReplacement;
        }

        public MutableComponent getDisplayName() {
            if (displayTranslationKey == null || displayTranslationKey.isBlank()) {
                return Component.translatable("gui." + DarkModeEverywhere.MODID + ".dark_mode");
            }
            return Component.translatable(displayTranslationKey);
        }
    }
}
