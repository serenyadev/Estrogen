package dev.mayaqq.estrogen.client.registry;

import com.google.gson.JsonParseException;
import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import dev.mayaqq.estrogen.Estrogen;
import dev.mayaqq.estrogen.client.cosmetics.render.CosmeticRenderLayer;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.flywheel.DreamData;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.flywheel.DreamType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.util.function.Function;

import static dev.mayaqq.estrogen.Estrogen.id;

public class EstrogenRenderer {

    // Inctance Types
    public static final StructType<DreamData> DREAM = new DreamType();

    // Partiql Models
    public static final PartialModel THIGH_HIGH = new PartialModel(id("trinket/thigh_high_base"));
    public static final PartialModel THIGH_HIGH_OVERLAY = new PartialModel(id("trinket/thigh_high_overlay"));
    public static final PartialModel CENTRIFUGE_COG = block("centrifuge/cog");

    // Buffer cache compartments
    public static final SuperByteBufferCache.Compartment<ResourceLocation> GENERIC = new SuperByteBufferCache.Compartment<>();

    // Outline stuff
    public static final ResourceLocation OUTLINE_POST_SHADER = Estrogen.id("shaders/post/outline.json");
    private static PostChain outlineChain;
    private static RenderTarget outlineTarget;

    public static RenderTarget getOutlineTarget() {
        return outlineTarget;
    }

    public static PostChain getOutlineChain() {
        return outlineChain;
    }

    private static PartialModel block(String path) {
        return new PartialModel(id("block/" + path));
    }

    public static void init() {
        CreateClient.BUFFER_CACHE.registerCompartment(GENERIC);

    }

    public static void reloadPostShaders() {
        Minecraft minecraft = Minecraft.getInstance();

        try {
            outlineChain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getMainRenderTarget(), OUTLINE_POST_SHADER);
            outlineChain.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            outlineTarget = outlineChain.getTempTarget("final");
        } catch (IOException | JsonParseException ex) {
            Estrogen.LOGGER.error("Failure", ex);
        }
    }

    public static void registerEntityLayers(Function<String, EntityRenderer<? extends Player>> getter) {
        PlayerRenderer wide = (PlayerRenderer) getter.apply("default");
        PlayerRenderer slim = (PlayerRenderer) getter.apply("slim");
        wide.addLayer(new CosmeticRenderLayer(wide));
        slim.addLayer(new CosmeticRenderLayer(slim));
    }
}
