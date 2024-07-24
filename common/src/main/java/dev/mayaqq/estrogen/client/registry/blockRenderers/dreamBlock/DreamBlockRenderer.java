package dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.mayaqq.estrogen.client.registry.EstrogenRenderType;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.texture.DreamBlockTexture;
import dev.mayaqq.estrogen.registry.blockEntities.DreamBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DreamBlockRenderer extends SafeBlockEntityRenderer<DreamBlockEntity> {

    public DreamBlockRenderer(BlockEntityRendererProvider.Context context) {}

    public void renderSafe(@NotNull DreamBlockEntity be, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        // if(Backend.canUseInstancing(be.getLevel())) return;
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(be, matrix4f, multiBufferSource.getBuffer(this.renderType()));
    }

    private void renderCube(DreamBlockEntity blockEntity, Matrix4f pose, VertexConsumer consumer) {
        float offsetDown = this.getOffsetDown();
        float offsetUp = this.getOffsetUp();
        renderPortalFace(blockEntity, pose, consumer, 1.0F, 0.0F, 1.0F, 1.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.SOUTH);
        renderPortalFace(blockEntity, pose, consumer, 0.0F, 1.0F, 0.0F, 0.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.NORTH);
        renderPortalFace(blockEntity, pose, consumer, 1.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.EAST);
        renderPortalFace(blockEntity, pose, consumer, 0.0F, 0.0F, 1.0F, 0.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.WEST);
        renderPortalFace(blockEntity, pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetDown, offsetDown, offsetDown, Direction.DOWN);
        renderPortalFace(blockEntity, pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, offsetUp, offsetUp, offsetUp, offsetUp, Direction.UP);
    }

    private static void renderPortalFace(DreamBlockEntity blockEntity, Matrix4f mat, VertexConsumer vertexConsumer, float x1, float x2, float z1, float z2, float y1, float y2, float y3, float y4, Direction dir) {
        addPortalVertex(vertexConsumer, mat, x1, y1, z1, y1 - 0.5f);
        addPortalVertex(vertexConsumer, mat, x2, y2, z1, y2 - 0.5f);
        addPortalVertex(vertexConsumer, mat, x2, y3, z2, y3 - 0.5f);
        addPortalVertex(vertexConsumer, mat, x1, y4, z2, y4 - 0.5f);
    }

    private static void addPortalVertex(VertexConsumer vertexConsumer, Matrix4f mat, float x, float y, float z, float p) {
        vertexConsumer.vertex(mat, x, y, z);
        vertexConsumer.uv(0.0f, p);
        vertexConsumer.endVertex();
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
