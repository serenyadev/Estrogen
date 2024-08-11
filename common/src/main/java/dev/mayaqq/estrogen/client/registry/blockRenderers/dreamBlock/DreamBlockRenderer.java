package dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.mayaqq.estrogen.client.registry.EstrogenRenderType;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.texture.DreamBlockTexture;
import dev.mayaqq.estrogen.registry.blockEntities.DreamBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DreamBlockRenderer extends SafeBlockEntityRenderer<DreamBlockEntity> {

    public DreamBlockRenderer(BlockEntityRendererProvider.Context context) {}

    public void renderSafe(@NotNull DreamBlockEntity be, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        // if(Backend.canUseInstancing(be.getLevel())) return;
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(be, matrix4f, multiBufferSource.getBuffer(this.renderType()));
    }

    private void renderCube(DreamBlockEntity blockEntity, Matrix4f pose, VertexConsumer consumer) {
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        this.renderFace(blockEntity, pose, consumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(DreamBlockEntity blockEntity, Matrix4f pose, VertexConsumer consumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, Direction direction) {

        addInnerVertex(blockEntity, pose, consumer, x0, y0, z0);
        addInnerVertex(blockEntity, pose, consumer, x1, y0, z1);
        addInnerVertex(blockEntity, pose, consumer, x1, y1, z2);
        addInnerVertex(blockEntity, pose, consumer, x0, y1, z3);

        if (!blockEntity.isTouchingDreamBlock(direction)) {
            addVertex(pose, consumer, x0, y1, z3, true);
            addVertex(pose, consumer, x1, y1, z2, true);
            addVertex(pose, consumer, x1, y0, z1, true);
            addVertex(pose, consumer, x0, y0, z0, true);
        }
    }

    /**
     * Vertices for the inner faces, which will have the shader applied.
     * Vertices are moved when there are neighboring dream blocks, so that their interiors connect.
     */
    private void addInnerVertex(DreamBlockEntity blockEntity, Matrix4f pose, VertexConsumer consumer, float x, float y, float z) {
        // ternary nightmare
        float x2 = blockEntity.isTouchingDreamBlock(x > 0.5 ? Direction.EAST : Direction.WEST) ? x : x * 7f/8f + 1f/16f;
        float y2 = blockEntity.isTouchingDreamBlock(y > 0.5 ? Direction.UP : Direction.DOWN) ? y : y * 7f/8f + 1f/16f;
        float z2 = blockEntity.isTouchingDreamBlock(z > 0.5 ? Direction.SOUTH : Direction.NORTH) ? z : z * 7f/8f + 1f/16f;

        addVertex(pose, consumer, x2, y2, z2, false);
    }

    private void addVertex(Matrix4f pose, VertexConsumer consumer, float x, float y, float z, boolean isBorder) {
        consumer.vertex(pose, x, y, z);
        if (isBorder) {
            consumer.color(255, 255, 255, 255);
        } else {
            consumer.color(0, 0, 0, 255);
        }
        consumer.endVertex();
    }

    public int getViewDistance() {
        return 256;
    }

    protected RenderType renderType() {
        return EstrogenRenderType.dreamBlock();
    }
}
