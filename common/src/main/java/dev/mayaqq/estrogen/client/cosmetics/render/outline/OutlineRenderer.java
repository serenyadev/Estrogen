package dev.mayaqq.estrogen.client.cosmetics.render.outline;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.mayaqq.estrogen.client.registry.EstrogenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11C.*;

public class OutlineRenderer implements MultiBufferSource {

    private static final OutlineRenderer INSTANCE = new OutlineRenderer();

    private final BufferBuilder builder;
    private final BufferBuilder outlineBuilder;
    private RenderType lastRenderType;

    public OutlineRenderer() {
        this.builder = new BufferBuilder(512);
        this.outlineBuilder = new BufferBuilder(512);
    }

    public static OutlineRenderer getInstance() {
        RenderSystem.assertOnRenderThread();
        return INSTANCE;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        builder.begin(renderType.mode(), renderType.format());
        outlineBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        lastRenderType = renderType;
        return VertexMultiConsumer.create(builder, new OutlineConsumer(outlineBuilder));
    }

    public void render() {
        Minecraft minecraft = Minecraft.getInstance();
        EstrogenRenderer.getOutlineTarget().bindWrite(false);
        //EstrogenRenderer.getOutlineTarget().copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
        GlStateManager._stencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        RenderSystem.enableDepthTest();

        GlStateManager._stencilFunc(GL_ALWAYS, 1, 0xFF);
        GlStateManager._stencilMask(0xFF);

        lastRenderType.setupRenderState();
        BufferUploader.drawWithShader(builder.end());
        lastRenderType.clearRenderState();

        GlStateManager._stencilFunc(GL_EQUAL, 0, 0xFF);
        GlStateManager._stencilMask(0x00);
        GlStateManager._disableDepthTest();
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        minecraft.gameRenderer.lightTexture().turnOnLightLayer();
        BufferUploader.drawWithShader(outlineBuilder.end());
        minecraft.gameRenderer.lightTexture().turnOffLightLayer();

        GlStateManager._stencilMask(0xFF);
        GlStateManager._stencilFunc(GL_ALWAYS, 0, 0xFF);
        GlStateManager._enableDepthTest();
        EstrogenRenderer.getOutlineTarget().unbindWrite();
        minecraft.getMainRenderTarget().bindWrite(false);
    }


    private static class OutlineConsumer extends DefaultedVertexConsumer {

        private final VertexConsumer delegate;

        private double x;
        private double y;
        private double z;

        public OutlineConsumer(VertexConsumer delegate) {
            this.delegate = delegate;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            delegate.vertex(x * 2, y * 2, z * 2);
            return this;
        }

        @Override
        public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
            delegate.vertex(matrix, x * 2, y * 2, z * 2);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            delegate.color(255, 255, 255, 255);
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
            super.vertex(x , y , z , 255, 255, 255, 255, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
        }

        @Override
        public void endVertex() {
            delegate.endVertex();
        }
    }
}
