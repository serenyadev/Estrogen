package dev.mayaqq.estrogen.client.cosmetics.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

import java.util.Map;

public class GroupedBakedCosmeticModel implements RenderableModel {

    private final Map<String, TransformableMesh> meshes;
    private final String name;
    private final Vector3f minBounds;
    private final Vector3f maxBounds;

    public GroupedBakedCosmeticModel(Map<String, TransformableMesh> meshes, Vector3f minBounds, Vector3f maxBounds, String name) {
        this.meshes = meshes;
        this.name = name;
        this.minBounds = minBounds;
        this.maxBounds = maxBounds;
    }

    @Override
    public void renderInto(VertexConsumer consumer, PoseStack transform, int color, int light, int overlay) {
        for(TransformableMesh mesh : meshes.values()) {
            transform.pushPose();
            mesh.applyTransforms(transform);
            mesh.bufferInto(consumer, transform, color, light, overlay);
            transform.popPose();
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
