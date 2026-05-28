package com.shiliuyinyu.examplemod.entity.spawner;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.entity.monster.FrostBeeEntity;
import com.shiliuyinyu.examplemod.entity.monster.FrostSpiritEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.ModifyCustomSpawnersEvent;

/**
 * 模组生物生成系统 —— 统一注册入口。
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>{@link RegisterSpawnPlacementsEvent}（Mod 总线）—— 注册生物生成位置规则，
 *       使自定义生物可以通过原版 {@link net.minecraft.world.level.NaturalSpawner} 自然生成。</li>
 *   <li>{@link ModifyCustomSpawnersEvent}（Game 总线）—— 将 {@link FrostBeeSpawner}
 *       添加到世界的自定义生成器列表中，由
 *       {@link net.minecraft.server.level.ServerLevel#tickCustomSpawners} 每 tick 调用。</li>
 * </ul>
 */
@EventBusSubscriber(modid = ExampleMod.MOD_ID)
public class ModSpawners {

    /**
     * Mod 总线事件：注册生物生成位置规则。
     * <p>
     * 使用 {@link EventBusSubscriber} 自动发现，无需在 {@link ExampleMod} 中手动注册。
     * 调用 {@link RegisterSpawnPlacementsEvent#register} 将生成谓词（predicate）、
     * 位置类型（placement type）和高度图（heightmap）绑定到对应的 {@code EntityType}。
     */
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.FROST_SPIRIT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FrostSpiritEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                ModEntities.FROST_BEE.get(),
                SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FrostBeeEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    /**
     * Game 总线事件：向每个 {@link net.minecraft.server.level.ServerLevel} 的世界添加自定义生成器。
     * <p>
     * 此方法通过 {@link ExampleMod} 构造方法中的
     * {@code NeoForge.EVENT_BUS.addListener(ModSpawners::onModifyCustomSpawners)} 注册。
     * <p>
     * 每个 {@code ServerLevel} 初始化时都会创建新的
     * {@link FrostBeeSpawner} 实例，保证生成器拥有独立的状态（如计时器）。
     */
    public static void onModifyCustomSpawners(ModifyCustomSpawnersEvent event) {
        event.addCustomSpawner(new FrostBeeSpawner());
    }
}
