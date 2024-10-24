package dev.mayaqq.estrogen.client.cosmetics;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.*;
import org.joml.Math;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public final class CosmeticModelBakery {

    private static final float RESCALE_22_5 = 1.0F / (float)Math.cos(0.39269909262657166) - 1.0F;
    private static final float RESCALE_45 = 1.0F / (float)Math.cos(0.7853981852531433) - 1.0F;
    private static final float PACK = 127.0f;
    private static final float UNPACK = 1.0f / PACK;

    private CosmeticModelBakery() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static BakedCosmeticModel bake(List<BlockElement> elements) {
        int vertices = elements.stream().mapToInt(e -> e.faces.size()).sum() * 4;
        int[] vertexData = new int[vertices * BakedCosmeticModel.STRIDE];

        // Reusing ALL the instances
        Vector4f position = new Vector4f();
        Vector3f normal = new Vector3f();
        Matrix4f modelMat = new Matrix4f();
        Matrix3f normalMat = new Matrix3f();

        // Bounds
        Vector3f minBound = new Vector3f(1f, 1f, 1f);
        Vector3f maxBound = new Vector3f();

        int index = 0;
        for(BlockElement element : elements) {

            float[] shape = setupShape(element.from, element.to);
            BlockElementRotation rot = element.rotation;

            // IGNORE IDEA ADVICE rot can very much be null
            if (rot != null) applyElementRotation(rot, modelMat, normalMat);

            for (Map.Entry<Direction, BlockElementFace> entry : element.faces.entrySet()) {
                Direction direction = entry.getKey();
                BlockElementFace face = entry.getValue();
                FaceInfo info = FaceInfo.fromFacing(direction);

                for (int i = 0; i < 4; i++) {
                    FaceInfo.VertexInfo vertex = info.getVertexInfo(i);
                    position.set(shape[vertex.xFace], shape[vertex.yFace], shape[vertex.zFace], 1f);
                    normal.set(direction.getStepX(), direction.getStepY(), direction.getStepZ());

                    if (rot != null) {
                        modelMat.transform(position);
                        normalMat.transform(normal);
                    }

                    // Update the model bounds
                    minBound.set(Math.min(minBound.x, position.x), Math.min(minBound.y, position.y), Math.min(minBound.z, position.z));
                    maxBound.set(Math.max(maxBound.x, position.x), Math.max(maxBound.y, position.y), Math.max(maxBound.z, position.z));

                    putVertex(vertexData, index, i, position, face.uv, normal);
                    index++;
                }
            }

            modelMat.identity();
            normalMat.identity();
        }

        return new BakedCosmeticModel(vertexData, vertices, minBound, maxBound);
    }

    private static float[] setupShape(Vector3f min, Vector3f max) {
        float[] fs = new float[Direction.values().length];
        fs[FaceInfo.Constants.MIN_X] = min.x() / 16.0F;
        fs[FaceInfo.Constants.MIN_Y] = min.y() / 16.0F;
        fs[FaceInfo.Constants.MIN_Z] = min.z() / 16.0F;
        fs[FaceInfo.Constants.MAX_X] = max.x() / 16.0F;
        fs[FaceInfo.Constants.MAX_Y] = max.y() / 16.0F;
        fs[FaceInfo.Constants.MAX_Z] = max.z() / 16.0F;
        return fs;
    }

    private static void applyElementRotation(BlockElementRotation rotation, Matrix4f modelMat, Matrix3f normalMat) {
        Vector3f origin = rotation.origin();
        modelMat.translate(origin.x, origin.y, origin.z);

        Quaternionf quat = axisRotation(rotation.axis(), rotation.angle());
        modelMat.rotate(quat);
        normalMat.rotate(quat);

        if (rotation.rescale()) {
            Vector3f scale = getRescaleVector(rotation.axis());
            scale.mul((Math.abs(rotation.angle()) == 22.5f) ? RESCALE_22_5 : RESCALE_45);
            modelMat.scale(scale.x, scale.y, scale.z);

            float nx = 1.0f / scale.x;
            float ny = 1.0f / scale.y;
            float nz = 1.0f / scale.z;
            float i = Mth.fastInvCubeRoot(nx * ny * nz);
            normalMat.scale(nx * i, ny * i, nz * i);
        }
        modelMat.translate(-origin.x, -origin.y, -origin.z);
    }

    private static Vector3f getRescaleVector(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Vector3f(0, 1f, 1f);
            case Y -> new Vector3f(1f, 0, 1f);
            case Z -> new Vector3f(1f, 1f, 0);
        };
    }

    private static Quaternionf axisRotation(Direction.Axis axis, float degrees) {
        return switch (axis) {
            case X -> new Quaternionf().rotationX(degrees * Mth.DEG_TO_RAD);
            case Y -> new Quaternionf().rotationY(degrees * Mth.DEG_TO_RAD);
            case Z -> new Quaternionf().rotationZ(degrees * Mth.DEG_TO_RAD);
        };
    }

    private static void putVertex(int[] data, int index, int indexInFace, Vector4f position, BlockFaceUV uv, Vector3f normal) {
        int pos = index * BakedCosmeticModel.STRIDE;
        float u = uv.getU(indexInFace) / 16f;
        float v = uv.getV(indexInFace) / 16f;

        data[pos] = Float.floatToRawIntBits(position.x);
        data[pos + 1] = Float.floatToRawIntBits(position.y);
        data[pos + 2] = Float.floatToRawIntBits(position.z);
        data[pos + 3] = Float.floatToRawIntBits(u);
        data[pos + 4] = Float.floatToRawIntBits(v);
        data[pos + 5] = packNormal(normal.x, normal.y, normal.z);
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
