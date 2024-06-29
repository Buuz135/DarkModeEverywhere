package com.buuz135.darkmodeeverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ShaderConfig {

    private List<ShaderValue> shaders;
    private int version;
    private String selectedShader;
    private static final File configFilePath = new File("config" + File.separator + "darkmodeeverywhereshaders.json");

    public ShaderConfig() {
        this.shaders = new ArrayList<>();
        this.version = 1;
        this.shaders.add(new ShaderValue(ResourceLocation.fromNamespaceAndPath("darkmodeeverywhere", "perfect_dark"), "gui.darkmodeeverywhere.perfect_dark", 16777215));
        this.shaders.add(new ShaderValue(ResourceLocation.fromNamespaceAndPath("darkmodeeverywhere", "less_perfect_dark"), "gui.darkmodeeverywhere.less_perfect_dark", 16777215));
        this.shaders.add(new ShaderValue(ResourceLocation.fromNamespaceAndPath("darkmodeeverywhere", "toasted_light"), "gui.darkmodeeverywhere.toasted_light", 16777215));
        this.selectedShader = null;
    }

    public List<ShaderValue> getShaders() {
        return shaders;
    }

    public void setSelectedShader(ResourceLocation resourceLocation){
        if (resourceLocation == null){
            selectedShader = null;
        } else {
            selectedShader = resourceLocation.toString();
        }
        DarkModeEverywhere.LOGGER.debug("Selected shader updated to {}", selectedShader);
        new Thread(ShaderConfig::createDefaultConfigFile).start();
    }

    public String getSelectedShader() {
        return selectedShader;
    }

    private static Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public static void load(){
        if (!configFilePath.exists()){
            createDefaultConfigFile();
        }
        Gson gson = createGson();
        try (FileReader reader = new FileReader(configFilePath)) {
            ClientProxy.CONFIG = gson.fromJson(reader, ShaderConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
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

    public static class ShaderValue {
        public ResourceLocation resourceLocation;
        public String displayName;
        public int darkColorReplacement;

        public ShaderValue(ResourceLocation resourceLocation, String displayName, int darkColorReplacement) {
            this.resourceLocation = resourceLocation;
            this.displayName = displayName;
            this.darkColorReplacement = darkColorReplacement;
        }
    }
}
