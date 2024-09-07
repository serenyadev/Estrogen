package dev.mayaqq.estrogen.fabric.client;

import com.jozufozu.flywheel.fabric.event.FlywheelEvents;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.mixin.client.WindowResizeMixin;
import dev.mayaqq.estrogen.Estrogen;
import dev.mayaqq.estrogen.client.command.EstrogenClientCommands;
import dev.mayaqq.estrogen.client.config.ConfigSync;
import dev.mayaqq.estrogen.client.cosmetics.Cosmetic;
import dev.mayaqq.estrogen.client.features.dash.DashOverlay;
import dev.mayaqq.estrogen.client.registry.EstrogenClientEvents;
import dev.mayaqq.estrogen.client.registry.EstrogenRenderer;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;

public class EstrogenFabricClientEvents {

    public static void register() {
        HudRenderCallback.EVENT.register((guiGraphics, delta) -> {
            DashOverlay.drawOverlay(guiGraphics);
        });
        ModConfigEvents.loading(Estrogen.MOD_ID).register(ConfigSync::onLoad);
        ModConfigEvents.reloading(Estrogen.MOD_ID).register(ConfigSync::onReload);

        EstrogenClientEvents.onRegisterParticles((particle, provider) -> ParticleFactoryRegistry.getInstance().register(particle, provider::create));
        FlywheelEvents.RELOAD_RENDERERS.register(event -> EstrogenClientEvents.onReloadRenderer(event.getWorld()));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            EstrogenClientEvents.onDisconnect();
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
            EstrogenClientCommands.register(dispatcher, new FabricClientCommandManager());
        });

        EstrogenClientEvents.registerModelLayer((location, definition) -> EntityModelLayerRegistry.registerModelLayer(location, definition::get));

        WorldRenderEvents.AFTER_SETUP.register(context -> {
            EstrogenRenderer.getOutlineTarget().clear(Minecraft.ON_OSX);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        });

        WorldRenderEvents.LAST.register(context -> {
            UIRenderHelper.CustomRenderTarget outline = EstrogenRenderer.getOutlineTarget();
            Minecraft minecraft = Minecraft.getInstance();

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.assertOnRenderThread();
            RenderSystem.enableDepthTest();
            GlStateManager._colorMask(true, true, true, false);
            GlStateManager._depthMask(false);
            GlStateManager._viewport(0, 0, outline.width, outline.height);

            ShaderInstance shaderInstance = minecraft.gameRenderer.blitShader;
            shaderInstance.setSampler("DiffuseSampler", outline.getColorTextureId());
            Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, outline.width, outline.height, 0.0F, 1000.0F, 3000.0F);
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.DISTANCE_TO_ORIGIN);
            if (shaderInstance.MODEL_VIEW_MATRIX != null) {
                shaderInstance.MODEL_VIEW_MATRIX.set((new Matrix4f()).translation(0.0F, 0.0F, -2000.0F));
            }

            if (shaderInstance.PROJECTION_MATRIX != null) {
                shaderInstance.PROJECTION_MATRIX.set(matrix4f);
            }

            shaderInstance.apply();
            float f = outline.width;
            float g = outline.height;
            float h = (float)outline.viewWidth / (float)outline.width;
            float i = (float)outline.viewHeight / (float)outline.height;
            Tesselator tesselator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(0.0, g, 0.0).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex(f, g, 0.0).uv(h, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex(f, 0.0, 0.0).uv(h, i).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0F, i).color(255, 255, 255, 255).endVertex();
            BufferUploader.draw(bufferBuilder.end());
            shaderInstance.clear();
            GlStateManager._depthMask(true);
            GlStateManager._colorMask(true, true, true, true);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();

            minecraft.getMainRenderTarget().bindWrite(false);
        });

    }

    public static class FabricClientCommandManager implements EstrogenClientCommands.ClientCommandManager<FabricClientCommandSource> {

        @Override
        public LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
            return ClientCommandManager.literal(name);
        }

        @Override
        public <I> RequiredArgumentBuilder<FabricClientCommandSource, I> argument(String name, ArgumentType<I> type) {
            return ClientCommandManager.argument(name, type);
        }

        @Override
        public boolean hasPermission(FabricClientCommandSource source, int permissionLevel) {
            return source.hasPermission(permissionLevel);
        }

        @Override
        public void sendFailure(FabricClientCommandSource source, Component component) {
            source.sendError(component);
        }
    }
}
