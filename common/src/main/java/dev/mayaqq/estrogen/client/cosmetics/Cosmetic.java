package dev.mayaqq.estrogen.client.cosmetics;

import com.google.gson.JsonObject;


import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.mayaqq.estrogen.client.registry.EstrogenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.function.Function;

public record Cosmetic(String id, String name, CosmeticTexture texture, CosmeticModel model) {

    public static Cosmetic fromJson(String id, JsonObject json) {
        String name = GsonHelper.getAsString(json, "name", id);
        CosmeticTexture texture = new CosmeticTexture(json.get("texture").getAsString());
        CosmeticModel model = new CosmeticModel(json.get("model").getAsString());

        return new Cosmetic(
                id,
                name,
                texture,
                model
        );
    }

    /**
     * Use this for rendering cosmetics
     * @param renderType Render type function, provides a RenderType for the texture, e.g. RenderType::entityCutout
     * @param source MultiBufferSource to render this cosmetic into
     * @param matrices PoseStack with transformations
     * @param light lighting
     */
    public void render(Function<ResourceLocation, RenderType> renderType, MultiBufferSource source, PoseStack matrices, int light, int overlay) {
        model.get().ifPresent(model -> model.renderInto(
            source.getBuffer(renderType.apply(texture.getResourceLocation())),
            matrices.last(),
            0xFFFFFFFF,
            light,
            overlay
        ));
    }

    public void renderImmediate(Tesselator tesselator, PoseStack matrices) {

        if(model.get().isEmpty()) return;

        if(EstrogenRenderer.getOutlineTarget() == null) EstrogenRenderer.reloadPostShaders();
        RenderTarget outlineTarge = EstrogenRenderer.getOutlineTarget();

        BufferBuilder buffer = tesselator.getBuilder();
        BakedCosmeticModel baked = model.get().get();

        outlineTarge.resize(Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight(), false);
        outlineTarge.bindWrite(false);
        RenderSystem.setShaderTexture(0, texture.getResourceLocation());
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        baked.renderInto(buffer, matrices.last(), 0xFFFFFFFF, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        tesselator.end();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        EstrogenRenderer.getOutlineChain().process(0);


    }

}
