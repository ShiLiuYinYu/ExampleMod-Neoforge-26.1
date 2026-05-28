package com.shiliuyinyu.examplemod;

import com.shiliuyinyu.examplemod.block.ModBlocks;
import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.entity.spawner.ModSpawners;
import com.shiliuyinyu.examplemod.item.ModCreativeModeTabs;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// 此处的值应与 META-INF/neoforge.mods.toml 文件中的条目一致
@Mod(ExampleMod.MOD_ID)
public class ExampleMod {
    // 在公共位置定义 mod id 供各处引用
    public static final String MOD_ID = "example";
    // 直接引用 slf4j 日志记录器
    public static final Logger LOGGER = LogUtils.getLogger();

    // 模组类的构造方法是模组加载时最先执行的代码。
    // FML 会自动识别 IEventBus、ModContainer 等参数类型并传入。
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册 commonSetup 方法用于模组加载
        modEventBus.addListener(this::commonSetup);

        // 注册物品
        ModItems.register(modEventBus);
        // 注册方块
        ModBlocks.register(modEventBus);
        // 注册物品栏
        ModCreativeModeTabs.register(modEventBus);
        // 注册实体 —— EntityType 在此绑定到注册表，属性通过 ModEntityAttributes 自动注册
        ModEntities.register(modEventBus);

        // 向 NeoForge 事件总线注册本类，以接收服务端和其他游戏事件。
        // 注意：仅当需要 *本类* (ExampleMod) 直接响应事件时才需要这行。
        // 如果本类中没有 @SubscribeEvent 注解的方法（如下方的 onServerStarting()），请删除这行。
        NeoForge.EVENT_BUS.register(this);

        // 注册自定义生物生成器 —— 在 ServerLevel 初始化时将 FrostSpiritSpawner 和 FrostBeeSpawner 注入世界
        NeoForge.EVENT_BUS.addListener(ModSpawners::onModifyCustomSpawners);

        // 向创造模式物品栏注册物品
        modEventBus.addListener(this::addCreative);

        // 注册模组的 ModConfigSpec，FML 会据此创建和加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 一些通用初始化代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 将模组物品添加到材料物品栏
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            event.accept(ModItems.ICE_ETHER.get());
            event.accept(ModItems.RAW_ICE_ETHER.get());
            event.accept(ModItems.CARDBOARD.get());
        }
        // 将刷怪蛋添加到原版刷怪蛋物品栏
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){
            event.accept(ModItems.FROST_SPIRIT_SPAWN_EGG.get());
            event.accept(ModItems.FROST_BEE_SPAWN_EGG.get());
        }
    }

    // 使用 @SubscribeEvent 让事件总线自动发现并调用方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 服务端启动时执行的操作
        LOGGER.info("HELLO from server starting");
    }
}
