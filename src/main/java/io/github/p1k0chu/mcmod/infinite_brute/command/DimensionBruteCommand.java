package io.github.p1k0chu.mcmod.infinite_brute.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.p1k0chu.mcmod.infinite_brute.BlockNotInWorld;
import io.github.p1k0chu.mcmod.infinite_brute.DimensionBrute;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class DimensionBruteCommand {
    private static final Component COMMAND_FAILED_COMPONENT = new TextComponent("Ran out of dimension without a find :<\n" +
            "Try again, it'll start over. If it fails anyway, maybe change the passphrase length.");
    private static final Component BLOCK_IS_NOT_FINDABLE = new TextComponent("Sorry, this command is not capable of finding this block, try another.");
    private static final Component CAN_ONLY_FIND_VANILLA_BIOME = new TextComponent("Sorry, this command can only find vanilla biomes.");
    private static final Component UNKNOWN_BIOME_SAD_FACE = new TextComponent("Unknown biome :<");
    private static final Component DIMENSION_BRUTE_RESET = new TextComponent("Dimension iterator was reset.");

    private static int passphraseLength = 3;
    private static DimensionBrute dimensionBrute;

    private DimensionBruteCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dimbrute")
                        .then(
                                Commands.literal("block")
                                        .then(
                                                Commands.argument("block", BlockStateArgument.block())
                                                        .executes(context -> {
                                                            BlockInput block = BlockStateArgument.getBlock(context, "block");
                                                            if (!isBlockFindable(block)) {
                                                                context.getSource().sendFailure(BLOCK_IS_NOT_FINDABLE);
                                                                return 1;
                                                            }
                                                            Optional<String> passphrase = getDimensionBrute(context.getSource().getLevel()).findBlock(block);
                                                            if (passphrase.isPresent()) {
                                                                giveBook(context.getSource(), block.getState().getBlock().getName().getString(), passphrase.get());
                                                                return 0;
                                                            } else {
                                                                context.getSource().sendFailure(COMMAND_FAILED_COMPONENT);
                                                                return 1;
                                                            }
                                                        })
                                        )
                        )
                        .then(
                                Commands.literal("biome")
                                        .then(
                                                Commands.argument("biome", ResourceLocationArgument.id())
                                                        .suggests(((context, builder) -> SharedSuggestionProvider.suggestResource(Registry.BIOME.keySet(), builder)))
                                                        .executes(context -> {
                                                            ResourceLocation identifier = ResourceLocationArgument.getId(context, "biome");
                                                            Optional<Component> validationError = validateBiomeIdentifier(identifier);
                                                            if (validationError.isPresent()) {
                                                                context.getSource().sendFailure(validationError.get());
                                                                return 1;
                                                            }
                                                            Biome biome = Registry.BIOME.get(identifier);
                                                            if (biome == null) {
                                                                context.getSource().sendFailure(UNKNOWN_BIOME_SAD_FACE);
                                                                return 1;
                                                            }
                                                            Optional<String> passphrase = getDimensionBrute(context.getSource().getLevel()).findBiome(biome);
                                                            if (passphrase.isPresent()) {
                                                                giveBook(context.getSource(), biome.getName().getString(), passphrase.get());
                                                                return 0;
                                                            } else {
                                                                context.getSource().sendFailure(COMMAND_FAILED_COMPONENT);
                                                                return 1;
                                                            }
                                                        })
                                        )
                        )
        );

        dispatcher.register(
                Commands.literal("dimbrutelength")
                        .then(
                                Commands.argument("length", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int length = IntegerArgumentType.getInteger(context, "length");
                                            if (length < 1) {
                                                context.getSource().sendFailure(new TextComponent("Invalid length: " + length));
                                                return 1;
                                            }
                                            passphraseLength = length;
                                            dimensionBrute = null;
                                            context.getSource().sendSuccess(new TextComponent("Passphrase length was set to " + length), true);
                                            return 0;
                                        })
                        )
        );

        dispatcher.register(
                Commands.literal("dimbrutereset")
                        .executes(context -> {
                            dimensionBrute = null;
                            context.getSource().sendSuccess(DIMENSION_BRUTE_RESET, true);
                            return 0;
                        })
        );
    }

    private static DimensionBrute getDimensionBrute(Level level) {
        if (dimensionBrute == null || !dimensionBrute.hasNext()) {
            dimensionBrute = new DimensionBrute(passphraseLength, level);
        }
        return dimensionBrute;
    }

    private static Optional<Component> validateBiomeIdentifier(ResourceLocation identifier) {
        if (!identifier.getNamespace().equals("minecraft")) {
            return Optional.of(CAN_ONLY_FIND_VANILLA_BIOME);
        }
        return Optional.empty();
    }

    private static boolean isBlockFindable(BlockInput block) {
        return OverworldGeneratorSettings.SAFE_BLOCKS.stream().map(BlockNotInWorld::new).anyMatch(block);
    }

    private static void giveBook(CommandSourceStack source, String title, String passphrase) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack book = DimensionBrute.makeBook(title, passphrase);
        book.setCount(book.getMaxStackSize());
        source.sendSuccess(new TextComponent("Success!\n+1 ")
                .append(book.getItem().getDescription())
                .append(": \"").append(book.getDisplayName()).append("\""), false);
        player.addItem(book);
    }

}
