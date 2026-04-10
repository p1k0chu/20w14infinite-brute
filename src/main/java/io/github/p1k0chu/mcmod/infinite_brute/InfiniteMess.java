package io.github.p1k0chu.mcmod.infinite_brute;

import io.github.p1k0chu.mcmod.infinite_brute.command.BiomeInfoCommand;
import io.github.p1k0chu.mcmod.infinite_brute.command.DimHashCommand;
import io.github.p1k0chu.mcmod.infinite_brute.command.DimInfoCommand;
import io.github.p1k0chu.mcmod.infinite_brute.command.DimensionBruteCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;

public class InfiniteMess implements ModInitializer {
    public static final String MOD_ID = "infinite-brute";

    @Override
    public void onInitialize() {
        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            BiomeInfoCommand.register(dispatcher);
            DimensionBruteCommand.register(dispatcher);
            DimHashCommand.register(dispatcher);
            DimInfoCommand.register(dispatcher);
        });
    }
}
