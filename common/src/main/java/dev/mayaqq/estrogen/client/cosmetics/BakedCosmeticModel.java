package dev.mayaqq.estrogen.client.cosmetics;

import com.jozufozu.flywheel.core.model.BlockModel;
import com.jozufozu.flywheel.core.model.Model;
import com.mojang.blaze3d.vertex.*;
import dev.mayaqq.estrogen.client.cosmetics.models.RenderableModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import static dev.mayaqq.estrogen.client.cosmetics.CosmeticModelBakery.*;

public class BakedCosmeticModel implements RenderableModel {

    private static final ThreadLocal<BufferBuilder> LOCAL_BUILDER = ThreadLocal.withInitial(() -> new BufferBuilder(512));

    private final String name;
    private final int[] data;
    private final int vertexCount;
    public final Vector3f minBound;
    public final Vector3f maxBound;

    public BakedCosmeticModel(String name, int[] data, int vertexCount, Vector3f minBound, Vector3f maxBound) {
        this.name = name;
        this.data = data;
        this.vertexCount = vertexCount;
        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    @Override
    public void renderInto(VertexConsumer consumer, PoseStack transform, int color, int light, int overlay) {
        for(int vertex = 0; vertex < vertexCount; vertex++) {
            int pos = STRIDE * vertex;
            float x = Float.intBitsToFloat(data[pos]);
            float y = Float.intBitsToFloat(data[pos + 1]);
            float z = Float.intBitsToFloat(data[pos + 2]);
            float u = Float.intBitsToFloat(data[pos + 3]);
            float v = Float.intBitsToFloat(data[pos + 4]);
            int normal = data[pos + 5];

            if(transform != null) {
                consumer.vertex(transform.last().pose(), x, y, z);
            } else consumer.vertex(x, y, z);

            consumer.color(color);
            consumer.uv(u, v);
            consumer.overlayCoords(overlay);
            consumer.uv2(light);

            if(transform != null) {
                consumer.normal(transform.last().normal(), unpackNX(normal), unpackNY(normal), unpackNZ(normal));
            } else consumer.normal(unpackNX(normal), unpackNY(normal), unpackNZ(normal));

            consumer.endVertex();
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
