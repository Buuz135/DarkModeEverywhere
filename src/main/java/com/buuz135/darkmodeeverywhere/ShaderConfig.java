package com.buuz135.darkmodeeverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        this.shaders.add(new ShaderValue(new ResourceLocation("darkmodeeverywhere", "perfect_dark"), Component.translatable("gui.darkmodeeverywhere.perfect_dark"), 16777215));
        this.shaders.add(new ShaderValue(new ResourceLocation("darkmodeeverywhere", "less_perfect_dark"), Component.translatable("gui.darkmodeeverywhere.less_perfect_dark"), 16777215));
        this.shaders.add(new ShaderValue(new ResourceLocation("darkmodeeverywhere", "toasted_light"), Component.translatable("gui.darkmodeeverywhere.toasted_light"), 16777215));
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
        DarkModeEverywhere.LOGGER.debug("Selected shader updated to " + selectedShader);
        new Thread(ShaderConfig::createDefaultConfigFile).start();
    }

    public String getSelectedShader() {
        return selectedShader;
    }

    public static void load(){
        if (!configFilePath.exists()){
            createDefaultConfigFile();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileReader reader = new FileReader(configFilePath);
            ClientProxy.CONFIG = gson.fromJson(reader, ShaderConfig.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultConfigFile();
        }
    }

    private static void createDefaultConfigFile(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(ShaderConfig.configFilePath);
            gson.toJson(ClientProxy.CONFIG, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ShaderValue {
        public ResourceLocation resourceLocation;
        public Component displayName;
        public int darkColorReplacement;

        public ShaderValue(ResourceLocation resourceLocation, Component displayName, int darkColorReplacement) {
            this.resourceLocation = resourceLocation;
            this.displayName = displayName;
            this.darkColorReplacement = darkColorReplacement;
        }
    }
}
