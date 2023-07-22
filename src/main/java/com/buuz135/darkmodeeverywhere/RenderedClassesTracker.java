package com.buuz135.darkmodeeverywhere;

import com.mojang.logging.LogUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;

public class RenderedClassesTracker {

    private static final Set<String> TRACKED = new HashSet<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    private static void dumpAndClearTracker() {
        if ((!DarkConfig.CLIENT.METHOD_SHADER_DUMP.get()) || TRACKED.isEmpty()) return;

        LOGGER.info("--------------------------------------------------");
        TRACKED.forEach(LOGGER::info);
        LOGGER.info("--------------------------------------------------");
        TRACKED.clear();
    }

    private static void task() {
        while (true) {
            dumpAndClearTracker();
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void start() {
        new Thread(RenderedClassesTracker::task).start();
    }
    public static void add(String element){
        if (DarkConfig.CLIENT.METHOD_SHADER_DUMP.get()) TRACKED.add(element);
    }

}
