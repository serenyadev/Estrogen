package dev.mayaqq.estrogen.client.cosmetics.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public final class TransformableMesh {

    private final int[] vertices;
    private final int vertexCount;

    public float xOff;
    public float yOff;
    public float zOff;

    public float xRot;
    public float yRot;
    public float zRot;

    public float xScale;
    public float yScale;
    public float zScale;

    public TransformableMesh(int[] vertices, int vertexCount, @Nullable PartPose initialPose) {
        this.vertices = vertices;
        this.vertexCount = vertexCount;
        if(initialPose != null) {
            this.xOff = initialPose.x;
            this.yOff = initialPose.y;
            this.zOff = initialPose.z;
            this.xRot = initialPose.xRot;
            this.yRot = initialPose.yRot;
            this.zRot = initialPose.zRot;
        }
    }

    public Vector3f getMinBound() {
        return new Vector3f(minBound);
    }

    public Vector3f getMaxBound() {
        return new Vector3f(maxBound);
    }

    public void bufferInto(PoseStack transforms, VertexConsumer consumer, int color, int light, int overlay) {
        for(int i = 0; i < vertexCount; i++) {
        }
    }
}
