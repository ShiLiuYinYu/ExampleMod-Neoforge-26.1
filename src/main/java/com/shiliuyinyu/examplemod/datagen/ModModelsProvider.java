package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.block.ModBlocks;
import com.shiliuyinyu.examplemod.block.custom.StrawberryCrop;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class ModModelsProvider extends ModelProvider {
    public ModModelsProvider(PackOutput output) {
        super(output, ExampleMod.MOD_ID);
    }


    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.ICE_ETHER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RAW_ICE_ETHER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CARDBOARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.STRAWBERRY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHEESE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CORN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ANTHRACITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ANTHRACITE2.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PROSPECTOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FIRE_ETHER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FIRE_ETHER_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.FIRE_ETHER_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.FIRE_ETHER_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.FIRE_ETHER_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.FIRE_ETHER_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.PICKAXE_AXE_ITEM.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.PICKAXE_AXE_ITEM2.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.ICE_ETHER_HELMET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ICE_ETHER_CHESTPLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ICE_ETHER_LEGGINGS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ICE_ETHER_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FROST_SPIRIT_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FROST_BEE_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);


        blockModels.createTrivialCube(ModBlocks.RAW_ICE_ETHER_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.ICE_ETHER_ORE.get());

        //利用family创建方块组，ICE_ETHER_BLOCK就不用单独创建方块模型了
        //blockModels.createTrivialCube(ModBlocks.ICE_ETHER_BLOCK.get());
        blockModels.family(ModBlocks.ICE_ETHER_BLOCK.get())
                .stairs(ModBlocks.ICE_ETHER_STAIRS.get())
                .slab(ModBlocks.ICE_ETHER_SLAB.get())
                .wall(ModBlocks.ICE_ETHER_WALL.get())
                .fence(ModBlocks.ICE_ETHER_FENCE.get())
                .fenceGate(ModBlocks.ICE_ETHER_FENCE_GATE.get())
                .button(ModBlocks.ICE_ETHER_BUTTON.get())
                .pressurePlate(ModBlocks.ICE_ETHER_PRESSURE_PLATE.get())
                .door(ModBlocks.ICE_ETHER_DOOR.get())
                .trapdoor(ModBlocks.ICE_ETHER_TRAPDOOR.get());

        blockModels.createCropBlock(ModBlocks.STRAWBERRY_CROP.get(), StrawberryCrop.AGE, 0,1,2,3,4,5);



    }




    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return ModItems.ITEMS.getEntries().stream();
    }


    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream();
    }
}
