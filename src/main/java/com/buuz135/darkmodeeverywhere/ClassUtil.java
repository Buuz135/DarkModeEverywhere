package com.buuz135.darkmodeeverywhere;

import com.mojang.blaze3d.systems.RenderSystem;
import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.stream.Stream;

public class ClassUtil {

    private static final String SET_SHADER_METHOD_NAME = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_157427_");

    private static String walkForCallerClassName(Stream<StackWalker.StackFrame> stackFrameStream) {
        return stackFrameStream
                .filter(frame -> !(
                        frame.getClassName().equals(GameRenderer.class.getName())
                                || frame.getClassName().equals(ClassUtil.class.getName())
                                || frame.getClassName().equals(RenderSystem.class.getName())
                                || frame.getClassName().startsWith("java.lang.Thread")
                                || frame.getMethodName().equals(SET_SHADER_METHOD_NAME)
                                || frame.getClassName().equals(GuiComponent.class.getName())
                ))
                .findFirst()
                .map(f -> f.getClassName() + ":" + f.getMethodName())
                .orElse(null);
    }

    public static String getCallerClassName() {
        return StackWalker.getInstance()
                .walk(ClassUtil::walkForCallerClassName);
    }
}
