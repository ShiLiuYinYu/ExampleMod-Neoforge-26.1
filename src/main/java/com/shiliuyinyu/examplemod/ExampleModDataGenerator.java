package com.shiliuyinyu.examplemod;

import com.shiliuyinyu.examplemod.datagen.*;
import java.util.Set;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

@EventBusSubscriber(modid = ExampleMod.MOD_ID)
public class ExampleModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(((output, lookupProvider) ->
                new LootTableProvider(output, Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(
                                ModBlockLootTablesProvider::new, LootContextParamSets.BLOCK
                        ),
                        new LootTableProvider.SubProviderEntry(
                                ModEntityLootTablesProvider::new, LootContextParamSets.ENTITY
                        )
                ), lookupProvider)));

        event.createProvider(ModRecipesProvider.Runner::new);
        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModEnUsLangProvider::new);
        event.createProvider(ModZhCnLangProvider::new);
        event.createProvider(ModModelsProvider::new);
        event.createProvider(ModDataMapsProvider::new);
        event.createProvider(ModEquipmentAssetsProvider::new);

        // 注册群系生物生成数据 —— 将 FrostSpirit 添加到寒冷群系的自然生成列表中
        event.createProvider((output, registries) ->
                new DatapackBuiltinEntriesProvider(output, registries, ModBiomeModifiersProvider.BUILDER, Set.of(ExampleMod.MOD_ID)));
    }
}