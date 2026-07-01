package com.buuz135.darkmodeeverywhere.fabric;

import com.buuz135.darkmodeeverywhere.DarkModeEverywhere;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FabricModResourcePackSource implements RepositorySource {

    private static final String SHADER_RESOURCE = "assets/" + DarkModeEverywhere.MODID + "/shaders/core/dark_gui.fsh";

    public static RepositorySource[] appendTo(RepositorySource[] sources) {
        RepositorySource[] expanded = Arrays.copyOf(sources, sources.length + 1);
        expanded[sources.length] = new FabricModResourcePackSource();
        return expanded;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        FabricLoader.getInstance().getModContainer(DarkModeEverywhere.MODID)
                .flatMap(FabricModResourcePackSource::findResourceRoot)
                .map(this::createPack)
                .ifPresent(consumer);
    }

    private static Optional<Path> findResourceRoot(ModContainer modContainer) {
        Optional<Path> shaderPath = modContainer.findPath(SHADER_RESOURCE);
        if (shaderPath.isPresent()) {
            return findResourceRoot(shaderPath.get());
        }
        return modContainer.getRootPaths().stream()
                .filter(FabricModResourcePackSource::isResourceRoot)
                .findFirst();
    }

    private static Optional<Path> findResourceRoot(Path path) {
        Path current = Files.isRegularFile(path) ? path.getParent() : path;
        while (current != null) {
            if (isResourceRoot(current)) {
                return Optional.of(current);
            }
            current = current.getParent();
        }
        return Optional.empty();
    }

    private static boolean isResourceRoot(Path path) {
        return Files.exists(path.resolve(SHADER_RESOURCE));
    }

    private Pack createPack(Path resourceRoot) {
        PackLocationInfo locationInfo = new PackLocationInfo(
                DarkModeEverywhere.MODID,
                Component.literal("Dark Mode Everywhere"),
                PackSource.BUILT_IN,
                Optional.empty()
        );
        Pack.ResourcesSupplier resourcesSupplier = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo locationInfo) {
                return new PathPackResources(locationInfo, resourceRoot);
            }

            @Override
            public PackResources openFull(PackLocationInfo locationInfo, Pack.Metadata metadata) {
                return new PathPackResources(locationInfo, resourceRoot);
            }
        };
        Pack.Metadata metadata = new Pack.Metadata(
                Component.literal("Dark Mode Everywhere resources"),
                PackCompatibility.COMPATIBLE,
                FeatureFlagSet.of(),
                List.of()
        );
        return new Pack(
                locationInfo,
                resourcesSupplier,
                metadata,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, false)
        );
    }
}
