package com.shiliuyinyu.examplemod.item;

import com.google.common.collect.Maps;
import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.tag.ModItemTags;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.*;

import java.util.Map;

public interface ModArmorMaterials {

    ArmorMaterial ICE_ETHER = new ArmorMaterial(
            37, makeDefense(3,6,8,3, 11),15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,2f,1f,
            ModItemTags.ICE_ETHER_ARMOR_MATERIALS,
            createId("ice_ether")
    );


    private static Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm, int body) {
        return Maps.newEnumMap(
                Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helm, ArmorType.BODY, body)
        );
    }

    static ResourceKey<EquipmentAsset> createId(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, name));
    }
}
