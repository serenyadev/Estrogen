package dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.model.BlockModel;
import com.jozufozu.flywheel.core.model.Model;
import com.jozufozu.flywheel.core.model.ModelUtil;
import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.mayaqq.estrogen.client.EstrogenClient;
import dev.mayaqq.estrogen.client.registry.EstrogenRenderer;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.flywheel.DreamData;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.texture.advanced.DynamicDreamTexture;
import dev.mayaqq.estrogen.registry.blockEntities.DreamBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

public class DreamBlockInstance extends BlockEntityInstance<DreamBlockEntity> {

    protected DreamData data;

    public DreamBlockInstance(MaterialManager materialManager, DreamBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        if(!EstrogenClient.useAdvancedRenderer()) return;
        data = materialManager.cutout(DynamicDreamTexture.INSTANCE.getRenderType())
            .material(EstrogenRenderer.DREAM)
            .model(blockState, this::buildModel)
            .createInstance();

        data.setPosition(this.getInstancePosition())
            .setBlockLight(255)
            .setSkyLight(255);

        DynamicDreamTexture.addActive();
    }

    protected Model buildModel() {

        ShadeSeparatedBufferedData data = ModelUtil.getBufferedData((consumer, renderer, random) -> {
            this.face(consumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
            this.face(consumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
            this.face(consumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
            this.face(consumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
            this.face(consumer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
            this.face(consumer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);

        });

        return new BlockModel(data, "dream_block");
    }

    private void face(VertexConsumer builder, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, Direction direction) {
        addInnerVertex(builder, x0, y0, z0);
        addInnerVertex(builder, x1, y0, z1);
        addInnerVertex(builder, x1, y1, z2);
        addInnerVertex(builder, x0, y1, z3);

        if(!blockEntity.isTouchingDreamBlock(direction)) {
            addOuterVertex(builder, x0, y1, z3);
            addOuterVertex(builder, x1, y1, z2);
            addOuterVertex(builder, x1, y0, z1);
            addOuterVertex(builder, x0, y0, z0);
        }
    }

    /**
     * Vertices for the inner faces, which will have the shader applied.
     * Vertices are moved when there are neighboring dream blocks, so that their interiors connect.
     */
    private void addInnerVertex(VertexConsumer builder, float x, float y, float z) {
        // ternary nightmare
        float x2 = blockEntity.isTouchingDreamBlock(x > 0.5 ? Direction.EAST : Direction.WEST) ? x : x * 7f/8f + 1f/16f;
        float y2 = blockEntity.isTouchingDreamBlock(y > 0.5 ? Direction.UP : Direction.DOWN) ? y : y * 7f/8f + 1f/16f;
        float z2 = blockEntity.isTouchingDreamBlock(z > 0.5 ? Direction.SOUTH : Direction.NORTH) ? z : z * 7f/8f + 1f/16f;

        addVertex(builder, x2, y2, z2, false);
    }

    /**
     * Workaround to changing canOcclude() via config
     */
    private void addOuterVertex(VertexConsumer builder, float x, float y, float z) {
        float x2 = x * 0.999f + 0.0005f;
        float y2 = y * 0.999f + 0.0005f;
        float z2 = z * 0.999f + 0.0005f;

        addVertex(builder, x2, y2, z2, true);
    }

    private void addVertex(VertexConsumer builder, float x, float y, float z, boolean isBorder) {
        builder.vertex(x, y, z);
        if (isBorder) {
            builder.color(0xffffffff);
        } else {
            // Using normal to detect border here
            builder.color(0);
        }

        // TODO: Pos-Color vertex format
        builder.uv(0, 0)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0, 0, 0)
            .endVertex();
    }

    @Override
    protected void remove() {
        if(data == null) return;
        DynamicDreamTexture.removeActive();
        data.delete();
    }

    @Override
    public boolean shouldReset() {
        return super.shouldReset() || !EstrogenClient.useAdvancedRenderer();
    }
}
