package io.github.p1k0chu.mcmod.infinite_brute.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.dimension.DimHash;

import static io.github.p1k0chu.mcmod.infinite_brute.utils.ComponentUtils.hoverText;

public final class DimHashCommand {
    private DimHashCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dimhash")
                        .then(
                                Commands.argument("string", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            final int i = DimHash.getHash(StringArgumentType.getString(context, "string"));
                                            context.getSource().sendSuccess(
                                                    new TextComponent("Hash: ")
                                                            .append(new TextComponent(Integer.toString(i))
                                                                    .withStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Integer.toString(i)))
                                                                            .setHoverEvent(hoverText("Click to copy"))
                                                                            .setUnderlined(true)))
                                                            .append(" ")
                                                            .append(new TextComponent("[VIEW]")
                                                                    .withStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/diminfo " + i))
                                                                            .setBold(true)
                                                                            .setHoverEvent(hoverText("/diminfo " + i)))),
                                                    false
                                            );
                                            return 0;
                                        })
                        )
        );
    }
}
