package dev.mayaqq.estrogen.mixin.client;

import com.jozufozu.flywheel.core.model.ShadeSeparatingVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ShadeSeparatingVertexConsumer.class)
public class ShadeSeperatingVertexConsumerMixin {

    @Shadow protected VertexConsumer activeConsumer;

    @Inject(
        method = "prepare",
        at = @At("RETURN")
    )
    public void setDefaultConsumer(VertexConsumer shadedConsumer, VertexConsumer unshadedConsumer, CallbackInfo ci) {
        this.activeConsumer = shadedConsumer;
    }
}
