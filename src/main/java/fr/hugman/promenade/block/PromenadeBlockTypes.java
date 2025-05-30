package fr.hugman.promenade.block;

import com.mojang.serialization.MapCodec;
import fr.hugman.promenade.Promenade;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PromenadeBlockTypes {
    public static final MapCodec<BerryBushBlock> BERRY_BUSH = of("berry_bush", BerryBushBlock.CODEC);
    public static final MapCodec<MoaiBlock> MOAI = of("moai", MoaiBlock.CODEC);
    public static final MapCodec<HangingLeavesBlock> HANGING_LEAVES = of("hanging_leaves", HangingLeavesBlock.CODEC);
    public static final MapCodec<MapleLogBlock> MAPLE_LOG = of("maple_log", MapleLogBlock.CODEC);
    public static final MapCodec<PileBlock> PILE = of("pile", PileBlock.CODEC);
    public static final MapCodec<SnowyLeavesBlock> SNOWY_LEAVES = of("snowy_leaves", SnowyLeavesBlock.CODEC);
    public static final MapCodec<SnowyExtendedLeavesBlock> SNOWY_EXTENDED_LEAVES = of("snowy_extended_leaves", SnowyExtendedLeavesBlock.CODEC);
    public static final MapCodec<StrippedMapleLogBlock> STRIPPED_MAPLE_LOG = of("stripped_maple_log", StrippedMapleLogBlock.CODEC);
    public static final MapCodec<WitherRosePileBlock> WITHER_ROSE_PILE = of("wither_rose_pile", WitherRosePileBlock.CODEC);
    public static final MapCodec<NyliumBlock> NYLIUM = of("nylium", NyliumBlock.CODEC);

    private static <B extends Block> MapCodec<B> of(String path, MapCodec<B> blockType) {
        return Registry.register(Registries.BLOCK_TYPE, Promenade.id(path), blockType);
    }
}
