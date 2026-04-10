package io.github.p1k0chu.mcmod.infinite_brute.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;

import java.util.Iterator;

public final class ComponentUtils {
    private ComponentUtils() {
    }

    public static HoverEvent hoverText(Component component) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, component);
    }

    public static HoverEvent hoverText(String string) {
        return hoverText(new TextComponent(string));
    }

    public static void addToComponentFromIter(Component component, Iterator<Component> iterator, String prefix, String delimiter) {
        if (iterator.hasNext()) {
            component.append(prefix)
                    .append(iterator.next());
            while (iterator.hasNext()) {
                component.append(delimiter).append(iterator.next());
            }
        }
    }
}
