package com.buuz135.darkmodeeverywhere;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class RenderedClassesTracker {

    private static final HashMap<String, Boolean> TRACKED = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger("DME METHOD DUMP");

    public static void start(){
        new Thread(() -> {
            while (true) {
                if (DarkConfig.CLIENT.METHOD_SHADER_DUMP.get()){
                    LOGGER.info("--------------------------------------------------");
                    TRACKED.forEach((key, value) -> LOGGER.info(key));
                    LOGGER.info("--------------------------------------------------");
                    TRACKED.clear();
                }
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static void add(String element){
        if (DarkConfig.CLIENT.METHOD_SHADER_DUMP.get() && !TRACKED.containsKey(element)) TRACKED.put(element, true);
    }

}
