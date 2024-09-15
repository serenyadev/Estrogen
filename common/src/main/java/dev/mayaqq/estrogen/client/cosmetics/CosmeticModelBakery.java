package dev.mayaqq.estrogen.client.cosmetics;

import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.*;
import org.joml.Math;

import java.util.List;
import java.util.Map;

public final class CosmeticModelBakery {

    public static final int STRIDE = 6;

    private static final float RESCALE_22_5 = 1.0F / (float)Math.cos(0.39269909262657166) - 1.0F;
    private static final float RESCALE_45 = 1.0F / (float)Math.cos(0.7853981852531433) - 1.0F;
    private static final float PACK = 127.0f;
    private static final float UNPACK = 1.0f / PACK;

    private CosmeticModelBakery() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static BakedCosmeticModel bake(List<BlockElement> elements) {
        int vertices = elements.stream().mapToInt(e -> e.faces.size()).sum() * 4;
        int[] vertexData = new int[vertices * STRIDE];

        // Reusing ALL the instances
        Vector4f position = new Vector4f();
        Vector3f normal = new Vector3f();
        Matrix4f poseMat = new Matrix4f();
        Matrix3f normalMat = new Matrix3f();

        Vector4f minPos = new Vector4f();
        Vector4f maxPos = new Vector4f();
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();

        int index = 0;
        for(BlockElement element : elements) {

            float[] shape = setupShape(element.from, element.to);
            BlockElementRotation rot = element.rotation;

            minPos.set(element.from, 1.0f);
            maxPos.set(element.to, 1.0f);

            // IGNORE IDEA ADVICE rot can very much be null
            if (rot != null) {
                Vector3f origin = rot.origin();
                poseMat.translate(origin.x, origin.y, origin.z);
                Quaternionf quat = fromDirectionAxis(rot.axis()).rotationDegrees(rot.angle());
                poseMat.rotate(quat);
                normalMat.rotate(quat);
                if (rot.rescale()) {
                    Vector3f scale = getRescaleVector(rot.axis());
                    scale.mul((Math.abs(rot.angle()) == 22.5f) ? RESCALE_22_5 : RESCALE_45);
                    poseMat.scale(scale.x, scale.y, scale.z);

                    float nx = 1.0f / scale.x;
                    float ny = 1.0f / scale.y;
                    float nz = 1.0f / scale.z;
                    float i = Mth.fastInvCubeRoot(nx * ny * nz);
                    normalMat.scale(nx * i, ny * i, nz * i);
                }
                poseMat.translate(0 - origin.x, 0 - origin.y, 0 - origin.z);
                minPos.mul(poseMat);
                maxPos.mul(poseMat);
            }

            min.set(Math.min(min.x, minPos.x), Math.min(min.y, minPos.y), Math.min(min.z, minPos.z));
            max.set(Math.max(max.x, maxPos.x), Math.max(max.y, maxPos.y), Math.max(max.z, maxPos.z));

            for (Map.Entry<Direction, BlockElementFace> entry : element.faces.entrySet()) {
                Direction direction = entry.getKey();
                BlockElementFace face = entry.getValue();
                FaceInfo info = FaceInfo.fromFacing(direction);

                for (int i = 0; i < 4; i++) {
                    FaceInfo.VertexInfo vertex = info.getVertexInfo(i);
                    position.set(shape[vertex.xFace], shape[vertex.yFace], shape[vertex.zFace], 1f);
                    normal.set(direction.getStepX(), direction.getStepY(), direction.getStepZ());

                    if (rot != null) {
                        poseMat.transform(position);
                        normalMat.transform(normal);
                    }

                    putVertex(vertexData, index, position, face.uv, normal);
                    index++;
                }
            }

            poseMat.identity();
            normalMat.identity();
        }

        return new BakedCosmeticModel(vertexData, vertices, min, max);
    }

    private static void putVertex(int[] data, int index, Vector4f position, BlockFaceUV uv, Vector3f normal) {
        int pos = index * STRIDE;
        int quadIndex = index % 4;
        float u = uv.getU(quadIndex) / 16f;
        float v = uv.getV(quadIndex) / 16f;

        data[pos] = Float.floatToRawIntBits(position.x);
        data[pos + 1] = Float.floatToRawIntBits(position.y);
        data[pos + 2] = Float.floatToRawIntBits(position.z);
        data[pos + 3] = Float.floatToRawIntBits(u);
        data[pos + 4] = Float.floatToRawIntBits(v);
        data[pos + 5] = packNormal(normal.x, normal.y, normal.z);
    }

    private static float[] setupShape(Vector3f min, Vector3f max) {
        float[] fs = new float[Direction.values().length];
        fs[FaceInfo.Constants.MIN_X] = Math.min(min.x() / 16.0F, 999.0f);
        fs[FaceInfo.Constants.MIN_Y] = Math.min(min.y() / 16.0F, 999.0f);
        fs[FaceInfo.Constants.MIN_Z] = Math.min(min.z() / 16.0F, 999.0f);
        fs[FaceInfo.Constants.MAX_X] = Math.max(max.x() / 16.0F, -999.0f);
        fs[FaceInfo.Constants.MAX_Y] = Math.max(max.y() / 16.0F, -999.0f);
        fs[FaceInfo.Constants.MAX_Z] = Math.max(max.z() / 16.0F, -999.0F);
        return fs;
    }

    private static void updateBounds(Vector3f vec, Vector3f target) {
        if(vec.x < target.x && vec.y < target.y && vec.z < target.z) return;
        target.set(
            Math.max(vec.x, target.x),
            Math.max(vec.y, target.y),
            Math.max(vec.z, target.z)
        );
    }

    private static Vector3f getRescaleVector(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Vector3f(0, 1f, 1f);
            case Y -> new Vector3f(1f, 0, 1f);
            case Z -> new Vector3f(1f, 1f, 0);
        };
    }

    private static Axis fromDirectionAxis(Direction.Axis input) {
        return switch (input) {
            case X -> Axis.XP;
            case Y -> Axis.YP;
            case Z -> Axis.ZP;
        };
    }


    public static int packNormal(float x, float y, float z) {
        x = Mth.clamp(x, -1, 1);
        y = Mth.clamp(y, -1, 1);
        z = Mth.clamp(z, -1, 1);

        return ((int) (x * PACK) & 0xFF) | (((int) (y * PACK) & 0xFF) << 8) | (((int) (z * PACK) & 0xFF) << 16);
    }

    public static float unpackNX(int packedNormal) {
        return ((byte) (packedNormal & 0xFF)) * UNPACK;
    }

    public static float unpackNY(int packedNormal) {
        return ((byte) ((packedNormal >>> 8) & 0xFF)) * UNPACK;
    }

    public static float unpackNZ(int packedNormal) {
        return ((byte) ((packedNormal >>> 16) & 0xFF)) * UNPACK;
    }

    public static Vector3f unpackNormal(int packedNormal, Vector3f dest) {
        dest.set(unpackNX(packedNormal), unpackNY(packedNormal), unpackNZ(packedNormal));
        return dest;
    }
}
