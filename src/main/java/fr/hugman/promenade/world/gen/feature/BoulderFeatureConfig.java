package fr.hugman.promenade.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BoulderFeatureConfig(
        BlockStateProvider stateProvider,
        BlockPredicate replaceableBlocks,
        IntProvider radius
) implements FeatureConfig {
    public static final Codec<BoulderFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("state").forGetter((config) -> config.stateProvider),
            BlockPredicate.BASE_CODEC.fieldOf("replaceable").forGetter((config) -> config.replaceableBlocks),
            IntProvider.createValidatingCodec(1, 64).fieldOf("count").forGetter((config) -> config.radius)
    ).apply(instance, BoulderFeatureConfig::new));
}