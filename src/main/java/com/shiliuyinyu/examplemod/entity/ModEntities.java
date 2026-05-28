package com.shiliuyinyu.examplemod.entity;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.entity.monster.FrostBeeEntity;
import com.shiliuyinyu.examplemod.entity.monster.FrostSpiritEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组实体类型注册中心。
 * 相当于原版 {@link net.minecraft.world.entity.EntityType} 中集中注册所有实体的模式。
 * 每种自定义实体在这里声明一个 {@link DeferredHolder} 常量，
 * 然后在 {@link ExampleMod} 构造方法中调用 {@link #register} 完成注册。
 */
public class ModEntities {

    /** 实体类型注册器，目标注册表为 {@link Registries#ENTITY_TYPE} */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ExampleMod.MOD_ID);

    /**
     * 冰霜精灵 —— 敌对怪物，浮空移动，近战攻击玩家。
     * <ul>
     *   <li>分类: {@link MobCategory#MONSTER}</li>
     *   <li>碰撞箱: 0.6 × 0.8 格</li>
     *   <li>眼睛高度: 0.7 格（影响第一人称视角和射线检测）</li>
     *   <li>追踪范围: 8 区块（约 128 格，控制客户端渲染距离）</li>
     *   <li>更新间隔: 3 tick（每秒约 6.6 次同步，保证移动平滑）</li>
     * </ul>
     */
    public static final DeferredHolder<EntityType<?>, EntityType<FrostSpiritEntity>> FROST_SPIRIT =
            ENTITY_TYPES.register("frost_spirit",
                    () -> EntityType.Builder.of(FrostSpiritEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 0.8F)
                            .eyeHeight(0.7F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_spirit"))));

    /**
     * 冰霜蜜蜂 —— 飞行敌对怪物，近战攻击附带中毒。
     * <ul>
     *   <li>分类: {@link MobCategory#MONSTER}</li>
     *   <li>碰撞箱: 0.6 × 0.6 格</li>
     *   <li>眼睛高度: 0.4 格</li>
     *   <li>追踪范围: 8 区块</li>
     *   <li>更新间隔: 3 tick</li>
     * </ul>
     */
    public static final DeferredHolder<EntityType<?>, EntityType<FrostBeeEntity>> FROST_BEE =
            ENTITY_TYPES.register("frost_bee",
                    () -> EntityType.Builder.of(FrostBeeEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 0.6F)
                            .eyeHeight(0.4F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_bee"))));

    /**
     * 向 Mod 事件总线注册所有实体类型。
     * 必须在 {@link ExampleMod} 构造方法中调用。
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        ExampleMod.LOGGER.info("Registering ModEntities for " + ExampleMod.MOD_ID);
    }
}
