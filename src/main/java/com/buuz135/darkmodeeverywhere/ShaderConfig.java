package com.buuz135.darkmodeeverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShaderConfig {

    private List<Value> shaders;
    private int version;
    private String selectedShader;

    public ShaderConfig() {
        this.shaders = new ArrayList<>();
        this.version = 1;
        this.shaders.add(new Value(new ResourceLocation("darkmodeeverywhere", "perfect_dark"), "Perfect Dark", 16777215));
        this.shaders.add(new Value(new ResourceLocation("darkmodeeverywhere", "less_perfect_dark"), "Less Perfect Dark", 16777215));
        this.shaders.add(new Value(new ResourceLocation("darkmodeeverywhere", "toasted_light"), "Toasted Light Mode", 16777215));
        this.selectedShader = null;
    }

    public List<Value> getShaders() {
        return shaders;
    }

    public void setSelectedShader(ResourceLocation resourceLocation){
        if (resourceLocation == null){
            selectedShader = null;
        } else {
            selectedShader = resourceLocation.toString();
        }
        new Thread(() -> {
            createDefault(new File("config" + File.separator + "darkmodeeverywhereshaders.json"));
        }).start();
    }

    public String getSelectedShader() {
        return selectedShader;
    }

    public static void load(){
        File file = new File("config" + File.separator + "darkmodeeverywhereshaders.json");
        if (!file.exists()){
            createDefault(file);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileReader reader = new FileReader(file);
            ClientProxy.CONFIG = gson.fromJson(reader, ShaderConfig.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            createDefault(file);
        }
    }

    private static void createDefault(File file){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(file);
            gson.toJson(ClientProxy.CONFIG, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Value{

        public String resourceLocation;
        public String displayName;
        public int darkColorReplacement;

        public Value(ResourceLocation resourceLocation, String displayName, int darkColorReplacement) {
            this.resourceLocation = resourceLocation.toString();
            this.displayName = displayName;
            this.darkColorReplacement = darkColorReplacement;
        }
    }
}
