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
    private int selectedShaderIndex;
    private static final File configFilePath = new File("config" + File.separator + "darkmodeeverywhereshaders.json");

    public ShaderConfig() {
        this.shaders = new ArrayList<>();
        this.version = 2;
        ResourceLocation tex_shader_location = ResourceLocation.fromNamespaceAndPath("darkmodeeverywhere", "dark_position_tex");
        ResourceLocation tex_color_shader_location = ResourceLocation.fromNamespaceAndPath("darkmodeeverywhere", "dark_position_tex_color");
        this.shaders.add(null);
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, Component.translatable("gui.darkmodeeverywhere.perfect_dark"), (float)5.5, 16777215));
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, Component.translatable("gui.darkmodeeverywhere.less_perfect_dark"), (float)3.5, 16777215));
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, Component.translatable("gui.darkmodeeverywhere.toasted_light"), (float)2, 16777215));
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
                .registerTypeAdapter(MutableComponent.class, new Component.Serializer())
                .create();
    }

    public static void load(){
        if (!configFilePath.exists()){
            createDefaultConfigFile();
        }
        Gson gson = createGson();
        try (FileReader reader = new FileReader(configFilePath)) {
            ClientProxy.CONFIG = gson.fromJson(reader, ShaderConfig.class);
            if (ClientProxy.CONFIG.version != new ShaderConfig().version) {
                throw new Exception("Invalid config version.");
            }
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

    public static class ShaderValue {
        public ResourceLocation texShaderLocation;
        public ResourceLocation texColorShaderLocation;
        public MutableComponent displayName;
        public float divideFactor;
        public int darkColorReplacement;

        public ShaderValue(ResourceLocation texShaderLocation, ResourceLocation texColorShaderLocation, MutableComponent displayName, float divideFactor, int darkColorReplacement) {
            this.texShaderLocation = texShaderLocation;
            this.texColorShaderLocation = texColorShaderLocation;
            this.displayName = displayName;
            this.divideFactor = divideFactor;
            this.darkColorReplacement = darkColorReplacement;
        }
    }
}
