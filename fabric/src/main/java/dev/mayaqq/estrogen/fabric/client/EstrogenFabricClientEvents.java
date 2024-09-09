package dev.mayaqq.estrogen.fabric.client;

import com.jozufozu.flywheel.fabric.event.FlywheelEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.mayaqq.estrogen.Estrogen;
import dev.mayaqq.estrogen.client.command.EstrogenClientCommands;
import dev.mayaqq.estrogen.client.config.ConfigSync;
import dev.mayaqq.estrogen.client.features.dash.DashOverlay;
import dev.mayaqq.estrogen.client.registry.EstrogenClientEvents;
import dev.mayaqq.estrogen.client.registry.blockRenderers.dreamBlock.DreamBlockVBORenderer;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.network.chat.Component;

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

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            DreamBlockVBORenderer.INSTANCE.draw(context.camera(), null);
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
