package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.entity.ModEntities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 群系生物生成数据 —— 注册表构建器。
 *
 * <p>通过 {@link RegistrySetBuilder} 定义模组的群系修饰器（biome modifier），
 * 将自定义生物添加到对应群系的 {@link net.minecraft.world.level.biome.MobSpawnSettings} 生成列表中。
 *
 * <p>生成的 JSON 位于 {@code data/example/neoforge/biome_modifier/}。
 * 运行 {@code runData} 任务即可自动生成，<b>无需手动编写 JSON</b>。
 *
 * <h3>工作原理</h3>
 * 使用 {@link BiomeModifiers.AddSpawnsBiomeModifier} 向
 * {@code #c:is_cold/overworld} 标签覆盖的所有寒冷群系添加 FrostSpirit 的生成条目。
 * 该方式与僵尸、骷髅等原版生物完全相同 —— 走
 * {@link net.minecraft.world.level.NaturalSpawner} 的自然生成流程，
 * 受群系生成权重和生物上限约束。
 *
 * <p>在 {@link com.shiliuyinyu.examplemod.ExampleModDataGenerator} 中通过
 * {@code DatapackBuiltinEntriesProvider} 注册。
 *
 * @see BiomeModifiers.AddSpawnsBiomeModifier
 */
public class ModBiomeModifiersProvider {

    /** 寒冷主世界群系标签 —— 积雪平原、冰刺之地、积雪针叶林、冻洋等 */
    private static final TagKey<Biome> COLD_OVERWORLD = TagKey.create(
            Registries.BIOME, Identifier.fromNamespaceAndPath("c", "is_cold/overworld"));

    /** 群系修饰器构建器 **/
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                HolderGetter<Biome> biomes = bootstrap.lookup(Registries.BIOME);
                HolderSet<Biome> coldBiomes = biomes.getOrThrow(COLD_OVERWORLD);

                // FrostSpirit: 权重 80，每组 1~3 只（参照原版僵尸权重 100、骷髅 80）
                Identifier id = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "add_frost_spirit");
                ResourceKey<BiomeModifier> key = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, id);

                bootstrap.register(key,
                        BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                                coldBiomes,
                                new Weighted<>(
                                        new MobSpawnSettings.SpawnerData(
                                                (EntityType<?>) ModEntities.FROST_SPIRIT.get(), 1, 3),
                                        80)));
            });

    private ModBiomeModifiersProvider() {}
}
