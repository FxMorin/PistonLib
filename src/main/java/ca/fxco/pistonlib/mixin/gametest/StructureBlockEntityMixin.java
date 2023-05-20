package ca.fxco.pistonlib.mixin.gametest;

import ca.fxco.pistonlib.PistonLib;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Mixin(StructureBlockEntity.class)
public abstract class StructureBlockEntityMixin extends BlockEntity {

    public StructureBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Redirect(
            method = {
                    "isStructureLoadable",
                    "loadStructure(Lnet/minecraft/server/level/ServerLevel;Z)Z",
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;" +
                            "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"
            )
    )
    private Optional<StructureTemplate> getFromTestStructures(StructureTemplateManager instance,
                                                              ResourceLocation resourceLocation) {
        if (PistonLib.GAMETEST_ACTIVE && this.getLevel() instanceof ServerLevel serverLevel) {
            try {
                return Optional.of(getStructureTemplate(resourceLocation.getPath(), serverLevel));
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
            return Optional.empty();
        }
        return instance.get(resourceLocation);
    }

    // FabricAPI is being dumb and replaced the method... So now I need to copy-paste the vanilla one to use it...
    private static StructureTemplate getStructureTemplate(String string, ServerLevel serverLevel) {
        StructureTemplateManager structureTemplateManager = serverLevel.getStructureManager();
        Optional<StructureTemplate> optional = structureTemplateManager.get(new ResourceLocation(string));
        if (optional.isPresent()) {
            return optional.get();
        }
        String string2 = string + ".snbt";
        Path path = Paths.get(StructureUtils.testStructuresDir, string2);
        CompoundTag compoundTag = StructureUtils.tryLoadStructure(path);
        if (compoundTag != null) {
            return structureTemplateManager.readStructure(compoundTag);
        }
        throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
    }
}
