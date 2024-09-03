package dev.mayaqq.estrogen.client.cosmetics.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mayaqq.estrogen.client.cosmetics.CosmeticModelBakery;
import dev.mayaqq.estrogen.client.cosmetics.animations.Animatable;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public final class TransformableMesh implements Animatable {

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

    public void applyTransforms(PoseStack transforms) {
        if(xOff != 0 || yOff != 0 || zOff != 0) {
            transforms.translate(xOff / 16f, yOff / 16f, zOff / 16f);
        }
        if(xRot != 0 || yRot != 0 || zRot != 0) {
            transforms.mulPose(new Quaternionf().rotateXYZ(xRot * Mth.DEG_TO_RAD, yRot * Mth.DEG_TO_RAD, zRot * Mth.DEG_TO_RAD));
        }
        if(xScale != 1 || yScale != 1 || zScale != 1) {
            transforms.scale(xScale, yScale, zScale);
        }
    }

    public void bufferInto(VertexConsumer consumer, PoseStack transforms, int color, int light, int overlay) {
        for(int i = 0; i < vertexCount; i++) {
            int pos = i * CosmeticModelBakery.STRIDE;
            float x = Float.intBitsToFloat(vertices[pos]);
            float y = Float.intBitsToFloat(vertices[pos + 1]);
            float z = Float.intBitsToFloat(vertices[pos + 2]);
            float u = Float.intBitsToFloat(vertices[pos + 3]);
            float v = Float.intBitsToFloat(vertices[pos + 4]);
            int normal = vertices[pos + 5];
            float nx = CosmeticModelBakery.unpackNX(normal);
            float ny = CosmeticModelBakery.unpackNY(normal);
            float nz = CosmeticModelBakery.unpackNZ(normal);

            // TODO: apply material colors and diffuse
            consumer.vertex(transforms.last().pose(), x, y, z);
            consumer.color(color);
            consumer.uv(u, v);
            consumer.overlayCoords(overlay);
            consumer.uv2(light);
            consumer.normal(transforms.last().normal(), nx, ny, nz);
            consumer.endVertex();
        }
    }

    @Override
    public void updateOffset(Vector3f vec) {
        this.xOff += vec.x;
        this.yOff += vec.y;
        this.zOff += vec.z;
    }

    @Override
    public void updateRotation(Vector3f vec) {
        this.xRot += vec.x;
        this.yRot += vec.y;
        this.zRot += vec.z;
    }

    @Override
    public void updateScale(Vector3f vec) {
        this.xScale += vec.x;
        this.yScale += vec.y;
        this.zScale += vec.z;
    }
}
