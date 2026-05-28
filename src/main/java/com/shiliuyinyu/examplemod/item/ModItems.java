package com.shiliuyinyu.examplemod.item;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.block.ModBlocks;
import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.item.custom.ModFuelItem;
import com.shiliuyinyu.examplemod.item.custom.PickaxeAxeItem;
import com.shiliuyinyu.examplemod.item.custom.ProspectorItem;
import com.shiliuyinyu.examplemod.tag.ModBlockTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public  static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MOD_ID);

    public static final DeferredItem<Item> ICE_ETHER = ITEMS.registerSimpleItem("ice_ether");
    public static final DeferredItem<Item> RAW_ICE_ETHER = ITEMS.registerSimpleItem("raw_ice_ether");
    public static final DeferredItem<Item> CARDBOARD = ITEMS.registerSimpleItem("material/cardboard");

    public static final DeferredItem<Item> CORN = ITEMS.registerSimpleItem("corn",()->new Item.Properties().food(ModFoods.CORN));
    public static final DeferredItem<Item> STRAWBERRY = ITEMS.registerSimpleItem("strawberry",()->new Item.Properties().food(ModFoods.STRAWBERRY, ModConsumables.STRAWBERRY));
    public static final DeferredItem<Item> CHEESE = ITEMS.registerSimpleItem("cheese",()->new Item.Properties().food(ModFoods.CHEESE, ModConsumables.CHEESE));

    public static final DeferredItem<Item> ANTHRACITE = ITEMS.registerItem("anthracite",p->new ModFuelItem(p, 3200));
    public static final DeferredItem<Item> ANTHRACITE2 = ITEMS.registerSimpleItem("anthracite2");

    //探矿器
    public static final DeferredItem<Item> PROSPECTOR = ITEMS.registerItem("prospector", p->new ProspectorItem(p.durability(127)));

    public static final DeferredItem<Item> FIRE_ETHER = ITEMS.registerSimpleItem("fire_ether");
    //剑
    public static final DeferredItem<Item> FIRE_ETHER_SWORD = ITEMS.registerItem("fire_ether_sword",
            p->new Item(p.sword(ModToolMaterials.FIRE_ETHER, 3,-2.4f)));
    //镐子
    public static final DeferredItem<Item> FIRE_ETHER_PICKAXE = ITEMS.registerItem("fire_ether_pickaxe",
            p->new Item(p.pickaxe(ModToolMaterials.FIRE_ETHER, 1, -2.8f)));
    //铲子
    public static final DeferredItem<Item> FIRE_ETHER_SHOVEL = ITEMS.registerItem("fire_ether_shovel",
            p->new ShovelItem(ModToolMaterials.FIRE_ETHER, 1.5f, -3.0f, p));
    //斧头
    public static final DeferredItem<Item> FIRE_ETHER_AXE = ITEMS.registerItem("fire_ether_axe",
            p->new AxeItem(ModToolMaterials.FIRE_ETHER, 1.0f, -3.0f, p));
    //锄头
    public static final DeferredItem<Item> FIRE_ETHER_HOE = ITEMS.registerItem("fire_ether_hoe",
            p->new HoeItem(ModToolMaterials.FIRE_ETHER, -2, -1.0f, p));

    //镐斧
    //用这种自定义标签的方式，不能进行剥皮抛光和脱蜡的操作
    public static final DeferredItem<Item> PICKAXE_AXE_ITEM = ITEMS.registerItem("pickaxe_axe_item",
            p->new Item(p.tool(ModToolMaterials.FIRE_ETHER, ModBlockTags.PICKAXE_AXE_MINEABLE, 5,-2.4f,0)));
    //自定义的镐斧类
    public static final DeferredItem<Item> PICKAXE_AXE_ITEM2 = ITEMS.registerItem("pickaxe_axe_item2",
            p->new PickaxeAxeItem(ModToolMaterials.FIRE_ETHER, 5,-2.4f, p));

    //盔甲
    public static final DeferredItem<Item> ICE_ETHER_HELMET = ITEMS.registerItem("ice_ether_helmet",
            p->new Item(p.humanoidArmor(ModArmorMaterials.ICE_ETHER, ArmorType.HELMET)));
    public static final DeferredItem<Item> ICE_ETHER_CHESTPLATE = ITEMS.registerItem("ice_ether_chestplate",
            p->new Item(p.humanoidArmor(ModArmorMaterials.ICE_ETHER, ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> ICE_ETHER_LEGGINGS = ITEMS.registerItem("ice_ether_leggings",
            p->new Item(p.humanoidArmor(ModArmorMaterials.ICE_ETHER, ArmorType.LEGGINGS)));
    public static final DeferredItem<Item> ICE_ETHER_BOOTS = ITEMS.registerItem("ice_ether_boots",
            p->new Item(p.humanoidArmor(ModArmorMaterials.ICE_ETHER, ArmorType.BOOTS)));

    //草莓种子
    public static final DeferredItem<Item> STRAWBERRY_SEEDS = ITEMS.registerItem("strawberry_seeds",
            p->new BlockItem(ModBlocks.STRAWBERRY_CROP.get(),p.useItemDescriptionPrefix()));

    //刷怪蛋 —— MC 26.1: 通过 DataComponents.ENTITY_DATA 组件关联实体类型，而非构造函数传入
    public static final DeferredItem<Item> FROST_SPIRIT_SPAWN_EGG = ITEMS.registerItem("frost_spirit_spawn_egg",
            p -> new SpawnEggItem(p.useItemDescriptionPrefix()
                    .component(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntities.FROST_SPIRIT.get(), new CompoundTag()))));

    public static final DeferredItem<Item> FROST_BEE_SPAWN_EGG = ITEMS.registerItem("frost_bee_spawn_egg",
            p -> new SpawnEggItem(p.useItemDescriptionPrefix()
                    .component(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntities.FROST_BEE.get(), new CompoundTag()))));

    // 注册物品，需要在主类的构造方法中调用
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ExampleMod.LOGGER.info("Registering ModItems for " + ExampleMod.MOD_ID);
    }

}
