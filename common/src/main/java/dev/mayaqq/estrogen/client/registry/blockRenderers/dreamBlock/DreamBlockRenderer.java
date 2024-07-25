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
        float f = this.getOffsetDown();
        float g = this.getOffsetUp();
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        this.renderFace(blockEntity, pose, consumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, f, f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        this.renderFace(blockEntity, pose, consumer, 0.0f, 1.0f, g, g, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(DreamBlockEntity blockEntity, Matrix4f pose, VertexConsumer consumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, Direction direction) {
        if (blockEntity.shouldRenderFace(direction)) {
            addFrontVertex(pose, consumer, x0, y0, z0);
            addFrontVertex(pose, consumer, x1, y0, z1);
            addFrontVertex(pose, consumer, x1, y1, z2);
            addFrontVertex(pose, consumer, x0, y1, z3);
        }

        if (blockEntity.shouldRenderBack(direction)) {
            addBackVertex(pose, consumer, x0, y0, z0, direction);
            addBackVertex(pose, consumer, x1, y0, z1, direction);
            addBackVertex(pose, consumer, x1, y1, z2, direction);
            addBackVertex(pose, consumer, x0, y1, z3, direction);
        }
    }

    private void addFrontVertex(Matrix4f pose, VertexConsumer consumer, float x, float y, float z) {
        consumer.vertex(pose, x, y, z);
        consumer.color(0, 0, 0, 255);
        consumer.endVertex();
    }
    private void addBackVertex(Matrix4f pose, VertexConsumer consumer, float x, float y, float z, Direction direction) {
        var position = new Vec3(x, y, z)
                .subtract(0.5, 0.5, 0.5)
                .subtract(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5))
                .scale(0.1/0.5)
                .subtract(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.99))
                .add(new Vec3(x, y, z));
        consumer.vertex(pose, (float) position.x, (float) position.y, (float) position.z);
        consumer.color(255, 255, 255, 255);
        consumer.endVertex();
    }

    protected float getOffsetUp() {
        return 1.0F;
    }

    protected float getOffsetDown() {
        return 0.0F;
    }

    public int getViewDistance() {
        return 256;
    }

    protected RenderType renderType() {
        return EstrogenRenderType.dreamBlock();
    }
}
