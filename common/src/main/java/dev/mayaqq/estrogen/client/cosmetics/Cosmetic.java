package dev.mayaqq.estrogen.client.cosmetics;

import com.google.gson.JsonObject;


import com.mojang.blaze3d.vertex.*;
import dev.mayaqq.estrogen.client.cosmetics.render.outline.OutlineRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.function.Function;

public record Cosmetic(String id, String name, CosmeticTexture texture, CosmeticModel model, boolean outline) {

    public static Cosmetic fromJson(String id, JsonObject json) {
        String name = GsonHelper.getAsString(json, "name", id);
        CosmeticTexture texture = new CosmeticTexture(json.get("texture").getAsString());
        CosmeticModel model = new CosmeticModel(json.get("model").getAsString());

        return new Cosmetic(
                id,
                name,
                texture,
                model,
            true
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
        model.get().ifPresent(m -> renderInternal(m, renderType.apply(texture.getResourceLocation()), source, matrices, light, overlay));
    }

    private void renderInternal(BakedCosmeticModel model, RenderType renderType, MultiBufferSource defaultSource, PoseStack matrices, int light, int overlay) {
        VertexConsumer consumer = (outline) ? OutlineRenderer.getInstance().getBuffer(renderType) : defaultSource.getBuffer(renderType);

        model.renderInto(consumer, matrices.last(), 0xFFFFFFFF, light, overlay);
        if(outline) OutlineRenderer.getInstance().end();
    }



}
