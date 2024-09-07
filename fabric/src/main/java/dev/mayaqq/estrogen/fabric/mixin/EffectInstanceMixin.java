package dev.mayaqq.estrogen.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EffectInstance.class)
public class EffectInstanceMixin {

    @Redirect(
        method = "<init>",
        at = @At(
            value = "NEW",
            args = "class=net.minecraft.resources.ResourceLocation"
        )
    )
    public ResourceLocation redirect(String $, @Local(argsOnly = true) String name) {
        ResourceLocation temp = new ResourceLocation(name);
        return new ResourceLocation(temp.getNamespace(), "shaders/program/" + temp.getPath() + ".json");
    }

    @Redirect(
        method = "getOrCreate",
        at = @At(
            value = "NEW",
            args = "class=net.minecraft.resources.ResourceLocation"
        )
    )
    private static ResourceLocation redirect2(String $, @Local(argsOnly = true) String name, @Local(argsOnly = true) Program.Type programType) {
        ResourceLocation temp = new ResourceLocation(name);
        return new ResourceLocation(temp.getNamespace(), "shaders/program/" + temp.getPath() + programType.getExtension());
    }
}
