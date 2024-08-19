package dev.mayaqq.estrogen.platform.forge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class CommonPlatformImpl {
    public static Supplier<Item> createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Item.Properties properties) {
        return () -> new ForgeSpawnEggItem(type, primaryColor, secondaryColor, properties);
    }

    public static TagKey<Item> getShearsTag() {
        return TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("forge", "shears"));
    }
}
