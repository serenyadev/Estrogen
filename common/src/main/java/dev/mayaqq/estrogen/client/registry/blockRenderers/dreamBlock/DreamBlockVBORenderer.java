package dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.texture.advanced.DynamicDreamTexture;
import dev.mayaqq.estrogen.registry.blockEntities.DreamBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

public class DreamBlockVBORenderer {

    public static final DreamBlockVBORenderer INSTANCE = new DreamBlockVBORenderer();

    private static final ThreadLocal<BufferBuilder> LOCAL_BUILDERS = ThreadLocal.withInitial(() -> new BufferBuilder(1024));

    private final DynamicDreamTexture dreamAtlas = new DynamicDreamTexture(256, 256, 80, 100, 16);
    private final VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private final PoseStack poseStack = new PoseStack();

    private final List<RenderInfo> allDreamBlocks = new ArrayList<>();

    public DreamBlockVBORenderer() {
        dreamAtlas.prepare();
    }

    public void addRenderedBlock(DreamBlockEntity be, int uOffset, int vOffset) {
        Vec3 vec = new Vec3(be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ());
        allDreamBlocks.add(new RenderInfo(be, vec, uOffset, vOffset));
        redraw();
    }

    public void release() {
        buffer.close();
        dreamAtlas.release();
    }

    public void redraw() {
        BufferBuilder builder = LOCAL_BUILDERS.get();
        builder.setQuadSorting(VertexSorting.DISTANCE_TO_ORIGIN);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        poseStack.pushPose();

        for(RenderInfo render : allDreamBlocks) {
            Vec3 position = render.position();
            poseStack.translate(position.x, position.y, position.z);
            PoseStack.Pose pose = poseStack.last();

            this.renderFace(render.be, pose, builder, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, render.texU, render.texV, Direction.SOUTH);
            this.renderFace(render.be, pose, builder, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, render.texU, render.texV,  Direction.NORTH);
            this.renderFace(render.be, pose, builder, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, render.texU, render.texV,  Direction.EAST);
            this.renderFace(render.be, pose, builder, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, render.texU, render.texV,  Direction.WEST);
            this.renderFace(render.be, pose, builder, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, render.texU, render.texV,  Direction.DOWN);
            this.renderFace(render.be, pose, builder, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, render.texU, render.texV,  Direction.UP);

        }
        this.uploadBuffer(builder.end());
        poseStack.popPose();
    }

    private void uploadBuffer(BufferBuilder.RenderedBuffer renderedBuffer) {
        if(RenderSystem.isOnRenderThread()) {
            buffer.upload(renderedBuffer);
        } else {
            RenderSystem.recordRenderCall(() -> buffer.upload(renderedBuffer));
        }
    }

    private void renderFace(DreamBlockEntity be, PoseStack.Pose pose, VertexConsumer consumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float u0, float v0, Direction cull) {

        float u1 = u0 + (1f / dreamAtlas.atlasWidth);
        float v1 = v0 + (1f / dreamAtlas.atlasHeight);

        consumer.vertex(pose.pose(), x0, y0, z0).color(0xFFFFFFFF).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), x1, y0, z1).color(0xFFFFFFFF).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), x1, y1, z2).color(0xFFFFFFFF).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), x0, y1, z3).color(0xFFFFFFFF).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

    }

    public void draw() {
        if(buffer.isInvalid() || buffer.getFormat() == null) return;
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getRendertypeEntityTranslucentShader);
        RenderSystem.setShaderTexture(0, dreamAtlas.location());
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        buffer.bind();
        buffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), Objects.requireNonNull(RenderSystem.getShader()));
        VertexBuffer.unbind();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private record RenderInfo(DreamBlockEntity be, Vec3 position, int texU, int texV) {}


}
