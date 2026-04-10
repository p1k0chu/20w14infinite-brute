package io.github.p1k0chu.mcmod.infinite_brute.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.p1k0chu.mcmod.infinite_brute.utils.BiomeDetailsUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

import static io.github.p1k0chu.mcmod.infinite_brute.utils.ComponentUtils.addToComponentFromIter;

public final class DimInfoCommand {
    private static final Component COULD_NOT_FIND_DIMENSION = new TextComponent("Dimension with that ID could not be found :<");

    private DimInfoCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("diminfo")
                        .then(
                                Commands.argument("id", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            ResourceLocation identifier = ResourceLocationArgument.getId(context, "id");
                                            DimensionType type = Registry.DIMENSION_TYPE.get(identifier);
                                            if (type == null) {
                                                context.getSource().sendFailure(COULD_NOT_FIND_DIMENSION);
                                                return 1;
                                            }
                                            Dimension dim = type.create(context.getSource().getLevel());
                                            context.getSource().sendSuccess(
                                                    new TextComponent("Dimension id: " + type.getId() + "\n")
                                                            .append(getDimInfo(dim)),
                                                    false
                                            );
                                            return 0;
                                        })
                        )
                        .then(
                                Commands.literal("current")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            Dimension dim = player.getLevel().getDimension();
                                            context.getSource().sendSuccess(getDimInfo(dim), false);
                                            return 0;
                                        })
                        )
        );
    }

    private static Component getDimInfo(Dimension dim) {
        ChunkGenerator<?> generator = dim.createRandomLevelGenerator();
        ChunkGeneratorSettings settings = generator.getSettings();

        Component component = new TextComponent("\nDefault block: ")
                .append(settings.getDefaultBlock().getBlock().getName())
                .append("\nDefault fluid: ")
                .append(settings.getDefaultFluid().getBlock().getName());

        addToComponentFromIter(
                component,
                dim.getKnownBiomes()
                        .map(BiomeDetailsUtils::getBiomeDetails)
                        .iterator(),
                "\nBiomes:\n",
                "\n\n"
        );

        return component;
    }

}
