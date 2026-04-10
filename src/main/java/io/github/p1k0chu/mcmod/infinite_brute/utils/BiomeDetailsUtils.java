package io.github.p1k0chu.mcmod.infinite_brute.utils;

import com.mojang.datafixers.types.JsonOps;
import io.github.p1k0chu.mcmod.infinite_brute.mixin.BiomeAccessor;
import io.github.p1k0chu.mcmod.infinite_brute.mixin.WeighedRandomItemAccessor;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.p1k0chu.mcmod.infinite_brute.utils.ComponentUtils.addToComponentFromIter;
import static io.github.p1k0chu.mcmod.infinite_brute.utils.ComponentUtils.hoverText;

public final class BiomeDetailsUtils {
    private static final GenerationStep.Decoration[] FEASIBLE_DECORATION_STEPS = new GenerationStep.Decoration[] {
            GenerationStep.Decoration.LOCAL_MODIFICATIONS,
            GenerationStep.Decoration.SURFACE_STRUCTURES,
            GenerationStep.Decoration.TOP_LAYER_MODIFICATION
    };

    private BiomeDetailsUtils() {
    }

    public static Component getBiomeDetails(Biome biome, boolean showFeatures) {
        SurfaceBuilderConfiguration surfaceConfig = biome.getSurfaceBuilderConfig();
        Component topBlock = surfaceConfig.getTopMaterial().getBlock().getName();
        Component underBlock = surfaceConfig.getUnderMaterial().getBlock().getName();

        Component component = new TextComponent("Biome: ").append(biome.getName())
                .append("\n    Surface: ").append(topBlock).append(" + ").append(underBlock);

        getStructures(biome).ifPresent(string -> component.append("\n    Structures: ").append(string));
        addToComponentFromIter(component, getMobsDetails(biome).iterator(), "\n    Mobs: ", ", ");
        if (showFeatures) {
            addToComponentFromIter(component, getFeatures(biome).iterator(), "\n    Features:\n        ", "\n        ");
        }

        return component;
    }

    public static Component getBiomeDetails(Biome biome) {
        return getBiomeDetails(biome, false);
    }

    private static Stream<Component> getFeatures(Biome biome) {
        return Arrays.stream(FEASIBLE_DECORATION_STEPS)
                .map(biome::getFeaturesForStep)
                .flatMap(Collection::stream)
                .map(BiomeDetailsUtils::describeFeature);
    }

    private static Component describeFeature(ConfiguredFeature<?, ?> configuredFeature) {
        ResourceLocation identifier;
        if (configuredFeature.config instanceof DecoratedFeatureConfiguration) {
            identifier = Registry.FEATURE.getKey(((DecoratedFeatureConfiguration) configuredFeature.config).feature.feature);
        } else {
            identifier = Registry.FEATURE.getKey(configuredFeature.feature);
        }

        return new TextComponent(identifier == null ? "null" : identifier.toString())
                .withStyle(style -> style
                        .setHoverEvent(hoverText(new TextComponent(configuredFeature.serialize(JsonOps.INSTANCE).getValue().toString()))));
    }

    private static List<Component> getMobsDetails(Biome biome) {
        List<Component> components = new ArrayList<>();

        for (MobCategory category : MobCategory.values()) {
            List<Biome.SpawnerData> list = biome.getMobs(category);
            if (list.isEmpty()) continue;

            List<Biome.SpawnerData> spawnerDataList = list.stream()
                    .filter(data -> data.minCount > 0 && ((WeighedRandomItemAccessor) data).getWeight() > 0)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<Integer> weights = new ArrayList<>(spawnerDataList.size());
            int weightSum = 0;
            for (Biome.SpawnerData spawnerData : spawnerDataList) {
                int weight = ((WeighedRandomItemAccessor) spawnerData).getWeight();
                weights.add(weight);
                weightSum += weight;
            }

            int finalWeightSum = weightSum;
            IntStream.range(0, spawnerDataList.size())
                    .mapToObj(index -> new TextComponent(((float) weights.get(index) / finalWeightSum * 100) + "% ")
                            .append(spawnerDataList.get(index).type.getDescription()))
                    .reduce((l, r) -> l.append("\n").append(r))
                    .map(hover -> new TextComponent(category.getName())
                            .withStyle(style -> style.setHoverEvent(hoverText(hover))))
                    .ifPresent(components::add);
        }

        return components;
    }

    private static Optional<String> getStructures(Biome biome) {
        return ((BiomeAccessor) biome).getValidFeatureStarts().keySet().stream()
                .map(StructureFeature::getFeatureName)
                .reduce((l, r) -> l + ", " + r);
    }
}
