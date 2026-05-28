package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.block.ModBlocks;
import com.shiliuyinyu.examplemod.block.custom.StrawberryCrop;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/// 方块战利品列表生成器
public class ModBlockLootTablesProvider extends BlockLootSubProvider{

    public ModBlockLootTablesProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS,registries);
    }

    @Override
    protected void generate() {
        //掉落自身
        dropSelf(ModBlocks.ICE_ETHER_BLOCK.get());
        dropSelf(ModBlocks.RAW_ICE_ETHER_BLOCK.get());
        //矿物掉落
        // createOreDrop方法只能使它掉落一个物品，不能使它掉落多个物品
        //add(ModBlocks.ICE_ETHER_ORE.get(), createOreDrop(ModBlocks.ICE_ETHER_ORE.get(), ModItems.RAW_ICE_ETHER.get()));
        add(ModBlocks.ICE_ETHER_ORE.get(), createLikeCopperOreDrops(ModBlocks.ICE_ETHER_ORE.get(), ModItems.RAW_ICE_ETHER.get()));

        dropSelf(ModBlocks.ICE_ETHER_STAIRS.get());
        dropSelf(ModBlocks.ICE_ETHER_BUTTON.get());
        dropSelf(ModBlocks.ICE_ETHER_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.ICE_ETHER_WALL.get());
        dropSelf(ModBlocks.ICE_ETHER_FENCE.get());
        dropSelf(ModBlocks.ICE_ETHER_FENCE_GATE.get());
        dropSelf(ModBlocks.ICE_ETHER_TRAPDOOR.get());
        add(ModBlocks.ICE_ETHER_DOOR.get(), createDoorTable(ModBlocks.ICE_ETHER_DOOR.get()));
        add(ModBlocks.ICE_ETHER_SLAB.get(), createSlabItemTable(ModBlocks.ICE_ETHER_SLAB.get()));

        //草莓方块的战利品列表
        LootItemCondition.Builder isStarwberryMaxAge = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.STRAWBERRY_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StrawberryCrop.AGE, StrawberryCrop.MAX_AGE));
        add(ModBlocks.STRAWBERRY_CROP.get(), createCropDrops(ModBlocks.STRAWBERRY_CROP.get(), ModItems.STRAWBERRY.get(), ModItems.STRAWBERRY_SEEDS.get(), isStarwberryMaxAge));

    }

    // 这个方法来自原版的createCopperOreDrops方法
    protected LootTable.Builder createLikeCopperOreDrops(Block block, Item item) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                block,
                (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // 只返回我们模组中的方块，而不是所有方块
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }


}
