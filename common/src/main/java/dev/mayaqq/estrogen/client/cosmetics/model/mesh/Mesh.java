package dev.mayaqq.estrogen.client.cosmetics.model.mesh;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Mesh {

    int STRIDE = 6;

    void renderInto(VertexConsumer consumer, @NotNull PoseStack transform, int color, int light, int overlay);

    int vertexCount();
}