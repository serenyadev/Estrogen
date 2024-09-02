package dev.mayaqq.estrogen.client.cosmetics.models;

import com.jozufozu.flywheel.core.model.BlockModel;
import com.jozufozu.flywheel.core.model.Model;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.OverlayTexture;

public interface RenderableModel {

    ThreadLocal<BufferBuilder> LOCAL_BUILDER = ThreadLocal.withInitial(() -> new BufferBuilder(512));

    void renderInto(VertexConsumer consumer, PoseStack transform, int color, int light, int overlay);

    String getName();

    default Model createFlywheelModel() {
        BufferBuilder builder = LOCAL_BUILDER.get();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        renderInto(builder, new PoseStack(), 0xFFFFFFFF, 0, OverlayTexture.NO_OVERLAY);
        BufferBuilder.RenderedBuffer buffer = builder.end();
        BlockModel bm = new BlockModel(buffer.vertexBuffer(), buffer.indexBuffer(), buffer.drawState(), buffer.drawState().vertexCount(), getName());
        buffer.release();
        return bm;
    }
}
