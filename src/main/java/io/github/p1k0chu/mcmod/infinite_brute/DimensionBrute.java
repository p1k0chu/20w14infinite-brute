package io.github.p1k0chu.mcmod.infinite_brute;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.dimension.DimHash;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public final class DimensionBrute {
    private final Iterator<Dimension> dimensionIterator;

    public DimensionBrute(int passphraseLength, Level level) {
        dimensionIterator = generateDimensions(passphraseLength, level);
    }

    public static ItemStack makeBook(String title, String passphrase) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag bookTag = book.getOrCreateTag();

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(passphrase));
        bookTag.put("pages", pages);
        bookTag.putString("author", "Ellie__x3");
        if (title != null) {
            bookTag.putString("title", title);
        }
        return book;
    }

    public Optional<String> findBlock(Predicate<BlockInWorld> predicate) {
        while (dimensionIterator.hasNext()) {
            Dimension dimension = dimensionIterator.next();

            if (hasBlock(dimension, predicate)) {
                return Optional.of(DimHash.getLastPasshphrase());
            }
        }
        return Optional.empty();
    }

    public Optional<String> findBiome(Biome biome) {
        while(dimensionIterator.hasNext()) {
            Dimension dimension = dimensionIterator.next();
            if (dimension.getKnownBiomes().anyMatch(biome::equals)) {
                return Optional.of(DimHash.getLastPasshphrase());
            }
        }
        return Optional.empty();
    }

    public boolean hasNext() {
        return dimensionIterator.hasNext();
    }

    private static Iterator<Dimension> generateDimensions(int size, Level level) {
        return new Iterator<Dimension>() {
            private final byte[] bytes = new byte[size];
            private boolean _hasNext = true;

            @Override
            public boolean hasNext() {
                return _hasNext;
            }

            @Override
            public Dimension next() {
                if (!_hasNext) {
                    throw new IllegalStateException("doesn't have next");
                }
                int id = DimHash.getHash(Base64.getEncoder().encodeToString(bytes));
                increment();
                DimensionType dimensionType = Registry.DIMENSION_TYPE.get(new ResourceLocation("_generated", String.valueOf(id)));
                assert dimensionType != null;
                return dimensionType.create(level);
            }

            private void increment() {
                for (int i = bytes.length - 1; i >= 0; --i) {
                    if (++bytes[i] != 0) {
                        return;
                    }
                }
                _hasNext = false;
            }
        };
    }

    private static boolean hasBlock(Dimension dimension, Predicate<BlockInWorld> predicate) {
        ChunkGeneratorSettings settings = dimension.createRandomLevelGenerator().getSettings();
        if (predicate.test(new BlockNotInWorld(settings.getDefaultBlock()))) {
            return true;
        }
        if (predicate.test(new BlockNotInWorld(settings.getDefaultFluid()))) {
            return true;
        }

        return dimension.getKnownBiomes()
                .anyMatch(b -> hasBlock(b, predicate));
    }

    private static boolean hasBlock(Biome biome, Predicate<BlockInWorld> predicate) {
        return predicate.test(new BlockNotInWorld(biome.getSurfaceBuilderConfig().getTopMaterial()))
                || predicate.test(new BlockNotInWorld(biome.getSurfaceBuilderConfig().getUnderMaterial()));
    }
}
