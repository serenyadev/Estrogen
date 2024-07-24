package dev.mayaqq.estrogen.registry.blockEntities;

import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.texture.DreamBlockTexture;
import dev.mayaqq.estrogen.registry.EstrogenBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DreamBlockEntity extends BlockEntity {
    @Environment(EnvType.CLIENT)
    DreamBlockTexture texture;

    public DreamBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public DreamBlockEntity(BlockPos pos, BlockState state) {
        super(EstrogenBlockEntities.DREAM_BLOCK.get(), pos, state);
    }

    @Environment(EnvType.CLIENT)
    public void setTexture(@Nullable DreamBlockTexture tex) {
        if(tex != null) tex.init();
        this.texture = tex;
    }

    @Environment(EnvType.CLIENT)
    public DreamBlockTexture getTexture() {
        return texture;
    }

    @Environment(EnvType.CLIENT)
    public void updateTexture(boolean propagateUpDown) {
        if(this.texture != null)
            texture.redraw();

        if(propagateUpDown && level != null) {
            BlockEntity above = level.getBlockEntity(worldPosition.above());
            BlockEntity below = level.getBlockEntity(worldPosition.below());
            if(above instanceof DreamBlockEntity dUp) dUp.updateTexture(false);
            if(below instanceof DreamBlockEntity dDown) dDown.updateTexture(false);
        }

    }

    public boolean shouldRenderFace(Direction face) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), face, this.getBlockPos().relative(face));
    }
}
