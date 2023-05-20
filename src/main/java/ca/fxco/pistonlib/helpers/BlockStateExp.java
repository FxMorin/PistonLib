package ca.fxco.pistonlib.helpers;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

// BlockState Expression
public abstract class BlockStateExp {

    public static final BlockStateExp EMPTY = BlockStateExp.of(Blocks.AIR);

    public abstract boolean matches(BlockState state);

    public abstract String asString();

    public abstract CompoundTag write();

    public static BlockStateExp read(CompoundTag compoundTag) {
        if (compoundTag.contains("state")) {
            if (compoundTag.getBoolean("state")) { // blockstate
                HolderLookup<Block> holderLookup = BuiltInRegistries.BLOCK.asLookup();
                BlockState state = NbtUtils.readBlockState(holderLookup, compoundTag);
                return BlockStateExp.of(state);
            } else { // block
                if (!compoundTag.contains("Name", 8)) {
                    return EMPTY;
                }
                HolderLookup<Block> holderLookup = BuiltInRegistries.BLOCK.asLookup();
                ResourceLocation resourceLocation = new ResourceLocation(compoundTag.getString("Name"));
                Optional<? extends Holder<Block>> optional = holderLookup.get(ResourceKey.create(Registries.BLOCK, resourceLocation));
                if (optional.isEmpty()) {
                    return EMPTY;
                }
                return BlockStateExp.of(optional.get().value());
            }
        }
        return null;
    }

    static class BlockStateExpState extends BlockStateExp {

        private final BlockState blockState;

        private BlockStateExpState(BlockState state) {
            this.blockState = state;
        }

        @Override
        public boolean matches(BlockState state) {
            if (state.getBlock() != this.blockState.getBlock()) {
                return false;
            }
            for (Property<?> property : this.blockState.getProperties()) { // make sure all required properties match
                if (!state.hasProperty(property) || state.getValue(property) != this.blockState.getValue(property)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String asString() {
            return BlockStateParser.serialize(this.blockState);
        }

        @Override
        public CompoundTag write() {
            CompoundTag compoundTag = NbtUtils.writeBlockState(this.blockState);
            compoundTag.putBoolean("state", true);
            return compoundTag;
        }
    }

    static class BlockStateExpBlock extends BlockStateExp {

        private final Block block;

        private BlockStateExpBlock(Block block) {
            this.block = block;
        }

        @Override
        public boolean matches(BlockState state) {
            return state.getBlock().equals(this.block);
        }

        @Override
        public CompoundTag write() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            compoundTag.putBoolean("state", false);
            return compoundTag;
        }

        @Override
        public String asString() {
            return this.block.builtInRegistryHolder().unwrapKey().map(key -> key.location().toString())
                    .orElse("minecraft:air");
        }
    }

    public static BlockStateExp of(BlockState state, boolean useBlock) {
        return useBlock ? new BlockStateExpBlock(state.getBlock()) : new BlockStateExpState(state);
    }

    public static BlockStateExp of(BlockState state) {
        return new BlockStateExpState(state);
    }

    public static BlockStateExp of(Block block) {
        return new BlockStateExpBlock(block);
    }

}
