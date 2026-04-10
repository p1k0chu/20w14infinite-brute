package io.github.p1k0chu.mcmod.infinite_brute.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.p1k0chu.mcmod.infinite_brute.utils.BiomeDetailsUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerator;

public final class BiomeInfoCommand {
    private BiomeInfoCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("biomeinfo")
                        .then(
                                Commands.argument("id", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int id = IntegerArgumentType.getInteger(context, "id");
                                            context.getSource().sendSuccess(BiomeDetailsUtils.getBiomeDetails(BiomeGenerator.magicize(id), true), false);
                                            return 0;
                                        })
                        )
        );
        dispatcher.register(
                Commands.literal("getcurrent")
                        .then(
                                Commands.literal("biome")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            Biome biome = player.getLevel().getBiome(player.blockPosition());
                                            context.getSource().sendSuccess(BiomeDetailsUtils.getBiomeDetails(biome, true), false);
                                            return 0;
                                        })
                        )
        );
    }
}
