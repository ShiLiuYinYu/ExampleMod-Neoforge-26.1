package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.block.ModBlocks;
import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output) {
        super(output, ExampleMod.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.ICE_ETHER.get(), "冰以太");
        add(ModItems.RAW_ICE_ETHER.get(), "粗冰以太");
        add(ModItems.CARDBOARD.get(), "纸板");

        add(ModItems.STRAWBERRY.get(), "草莓");
        add(ModItems.CHEESE.get(), "奶酪");
        add(ModItems.CORN.get(), "玉米");

        add(ModItems.ANTHRACITE.get(), "无烟煤");
        add(ModItems.ANTHRACITE2.get(), "无烟煤2");

        add(ModItems.PROSPECTOR.get(), "探矿器");

        add(ModBlocks.ICE_ETHER_BLOCK.get(), "冰以太块");
        add(ModBlocks.RAW_ICE_ETHER_BLOCK.get(), "粗冰以太块");
        add(ModBlocks.ICE_ETHER_ORE.get(), "冰以太矿石");

        add(ModBlocks.ICE_ETHER_STAIRS.get(), "冰以太楼梯");
        add(ModBlocks.ICE_ETHER_SLAB.get(), "冰以太台阶");
        add(ModBlocks.ICE_ETHER_BUTTON.get(), "冰以太按钮");
        add(ModBlocks.ICE_ETHER_PRESSURE_PLATE.get(), "冰以太压力板");
        add(ModBlocks.ICE_ETHER_WALL.get(), "冰以太墙");
        add(ModBlocks.ICE_ETHER_FENCE.get(), "冰以太栅栏");
        add(ModBlocks.ICE_ETHER_FENCE_GATE.get(), "冰以太栅栏门");
        add(ModBlocks.ICE_ETHER_DOOR.get(), "冰以太门");
        add(ModBlocks.ICE_ETHER_TRAPDOOR.get(), "冰以太活板门");

        add(ModItems.FIRE_ETHER.get(), "火以太");
        add(ModItems.FIRE_ETHER_SWORD.get(), "火以太剑");
        add(ModItems.FIRE_ETHER_AXE.get(), "火以太斧");
        add(ModItems.FIRE_ETHER_PICKAXE.get(), "火以太镐");
        add(ModItems.FIRE_ETHER_SHOVEL.get(), "火以太锹");
        add(ModItems.FIRE_ETHER_HOE.get(), "火以太锄");

        add(ModItems.PICKAXE_AXE_ITEM.get(), "镐斧");
        add(ModItems.PICKAXE_AXE_ITEM2.get(), "镐斧2");

        add(ModItems.ICE_ETHER_HELMET.get(), "冰以太头盔");
        add(ModItems.ICE_ETHER_CHESTPLATE.get(), "冰以太胸甲");
        add(ModItems.ICE_ETHER_LEGGINGS.get(), "冰以太护腿");
        add(ModItems.ICE_ETHER_BOOTS.get(), "冰以太靴子");

        add(ModItems.STRAWBERRY_SEEDS.get(), "草莓种子");

        add("itemGroup.example", "示例模组");

        add(ModEntities.FROST_SPIRIT.get(), "冰霜精灵");
        add(ModItems.FROST_SPIRIT_SPAWN_EGG.get(), "冰霜精灵刷怪蛋");

        add(ModEntities.FROST_BEE.get(), "冰霜蜜蜂");
        add(ModItems.FROST_BEE_SPAWN_EGG.get(), "冰霜蜜蜂刷怪蛋");

        add("tooltip.example.pickaxe_axe_item.shift", "这是一个镐斧物品");
        add("tooltip.example.pickaxe_axe_item", "按住 §nSHIFT§r 查看更多信息");
    }
}
