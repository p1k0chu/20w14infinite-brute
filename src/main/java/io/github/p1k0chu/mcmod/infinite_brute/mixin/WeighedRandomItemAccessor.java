package io.github.p1k0chu.mcmod.infinite_brute.mixin;

import net.minecraft.util.WeighedRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeighedRandom.WeighedRandomItem.class)
public interface WeighedRandomItemAccessor {
    @Accessor
    int getWeight();
}
