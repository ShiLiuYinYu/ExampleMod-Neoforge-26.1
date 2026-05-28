package com.shiliuyinyu.examplemod.entity;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.entity.monster.FrostBeeEntity;
import com.shiliuyinyu.examplemod.entity.monster.FrostSpiritEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/**
 * 实体属性注册器。
 * 通过 {@link EntityAttributeCreationEvent} 事件将所有自定义实体的属性
 * 注册到游戏内置的属性映射中。
 * <p>
 * 使用 {@link EventBusSubscriber} 自动发现，无需手动在 Mod 构造方法中注册。
 */
@EventBusSubscriber(modid = ExampleMod.MOD_ID)
public class ModEntityAttributes {

    /**
     * 监听 {@link EntityAttributeCreationEvent}，将每种实体的 {@code AttributeSupplier}
     * 绑定到对应的 {@code EntityType}。
     * <p>
     * 新增实体时，在此方法中添加对应的 put 调用。
     */
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FROST_SPIRIT.get(), FrostSpiritEntity.createAttributes().build());
        event.put(ModEntities.FROST_BEE.get(), FrostBeeEntity.createAttributes().build());
    }
}
