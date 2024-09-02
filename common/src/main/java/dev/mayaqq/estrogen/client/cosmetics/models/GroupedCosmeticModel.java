package dev.mayaqq.estrogen.client.cosmetics.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mayaqq.estrogen.client.registry.entityRenderers.moth.MothAnimations;
import org.joml.Vector3f;

import java.util.Map;

public class GroupedCosmeticModel implements RenderableModel {

    public final Map<String, TransformableMesh> meshes;

    public GroupedCosmeticModel(Map<String, TransformableMesh> meshes, Vector3f minBounds, Vector3f maxBounds) {
        this.meshes = meshes;
    }

    @Override
    public void renderInto(VertexConsumer consumer, PoseStack transform, int color, int light, int overlay) {
    }

    @Override
    public String getName() {
        return "";
    }
}
