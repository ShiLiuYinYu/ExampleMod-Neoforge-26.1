package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

/**
 * 实体战利品表数据生成器。
 * <p>
 * 继承 {@link EntityLootSubProvider}，在 {@link #generate()} 中为每种实体定义掉落物。
 * 运行 {@code ./gradlew runData} 后生成到 {@code data/<mod_id>/loot_table/entities/}。
 */
public class ModEntityLootTablesProvider extends EntityLootSubProvider {

    public ModEntityLootTablesProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    /**
     * 定义所有自定义实体的战利品表。
     * <ul>
     *   <li>FrostSpirit: 掉落 1~3 个冰醚（Ice Ether），受抢夺附魔加成</li>
     * </ul>
     */
    @Override
    public void generate() {
        this.add(ModEntities.FROST_SPIRIT.get(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(ModItems.ICE_ETHER.get())
                                        // 随机数量 1~3
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                        // 抢夺附魔每级额外增加 0~1 个
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                )
                        )
        );

        this.add(ModEntities.FROST_BEE.get(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(ModItems.ICE_ETHER.get())
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                )
                        )
        );
    }

    /**
     * 返回已知的实体类型列表。
     * 只返回本 Mod 注册的实体，避免遍历原版所有实体。
     */
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.ENTITY_TYPES.getEntries().stream()
                .map(holder -> (EntityType<?>) holder.value());
    }
}
