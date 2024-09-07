package dev.mayaqq.estrogen.client.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.DreamBlockShader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class EstrogenRenderType extends RenderType {

    public static final Function<ResourceLocation, RenderType> DREAM_BLOCK = Util.memoize(tex ->
        RenderType.create(
            "dream_block",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            256,
            false,
           false,
            CompositeState.builder()
               .setShaderState(new ShaderStateShard(DreamBlockShader::getDreamShader))
              .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
              .createCompositeState(false)
        )
    );

    public static final Function<RenderType, RenderType> OUTLINE_OF = Util.memoize(renderType ->
        new WrappingRenderType(renderType,
            () -> EstrogenRenderer.getOutlineTarget().bindWrite(false),
            () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)
        )
    );

    public EstrogenRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static class WrappingRenderType extends RenderType {

        public WrappingRenderType(RenderType renderType, Runnable setupState, Runnable clearState) {
            super(renderType.toString(), renderType.format(), renderType.mode(), renderType.bufferSize(), renderType.affectsCrumbling(), false,
                () -> {
                setupState.run();
                renderType.setupRenderState();
                },
                () -> {
                renderType.clearRenderState();
                clearState.run();
                }
            );
        }
    }
}
