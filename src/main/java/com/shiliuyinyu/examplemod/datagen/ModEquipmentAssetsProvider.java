package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.ExampleMod;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.function.BiConsumer;

public class ModEquipmentAssetsProvider extends EquipmentAssetProvider {
    public ModEquipmentAssetsProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
        super.registerModels(output);
        bootstrap(output);
    }

    private static void bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> consumer) {
        consumer.accept(
                ResourceKey.create(EquipmentAssets.ROOT_ID,
                        Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "ice_ether")),
                onlyHumanoid(ExampleMod.MOD_ID, "ice_ether")
        );
    }

    public static EquipmentClientInfo onlyHumanoid(String namespace, String path) {
        return EquipmentClientInfo.builder()
                .addHumanoidLayers(Identifier.fromNamespaceAndPath(namespace, path))
                .build();
    }
}
