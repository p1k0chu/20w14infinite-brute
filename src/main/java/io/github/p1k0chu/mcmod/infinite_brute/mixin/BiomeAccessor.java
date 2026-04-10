package io.github.p1k0chu.mcmod.infinite_brute.mixin;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor
    Map<StructureFeature<?>, FeatureConfiguration> getValidFeatureStarts();
}
