package dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import dev.mayaqq.estrogen.Estrogen;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class DreamBlockShader {
    @Nullable
    private static ShaderInstance shaderInstance;
    public static final ResourceLocation DREAM_TEXTURE_LOCATION = Estrogen.id("textures/entity/dream.png");

    private DreamBlockShader() {}

    @Nullable
    public static ShaderInstance getDreamShader() {
        return shaderInstance;
    }

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            context.register(
                    Estrogen.id("rendertype_estrogen_dream"),
                    DefaultVertexFormat.BLOCK,
                    program -> {
                        shaderInstance = program;
                    }
            );
        });
    }
}