package dev.mayaqq.estrogen.client.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.DreamBlockShader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

import java.util.function.Supplier;

@Environment(value= EnvType.CLIENT)
public class EstrogenRenderType extends RenderType {

    public EstrogenRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static final RenderType DREAM_BLOCK = RenderType.create(
            "dream_block",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            CompositeState
                    .builder()
                    .setShaderState(new ShaderStateShard(DreamBlockShader::getDreamShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(DreamBlockShader.DREAM_TEXTURE_LOCATION, false, false))
                    .createCompositeState(false)
    );

    public static RenderType dreamBlock() {return DREAM_BLOCK;}
}
