package com.shiliuyinyu.examplemod.item;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


/// 模组物品栏
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExampleMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
            CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
                            //物品栏名称 可以在语言文件中配置
                            .title(Component.translatable("itemGroup.example"))
                            // withTabsBefore与withTabsAfter不能更改原版的物品栏顺序
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(()-> new ItemStack(ModItems.ICE_ETHER.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.ICE_ETHER);
                                output.accept(ModItems.RAW_ICE_ETHER);
                                output.accept(ModItems.CARDBOARD);

                                output.accept(ModBlocks.ICE_ETHER_BLOCK);
                                output.accept(ModBlocks.RAW_ICE_ETHER_BLOCK);
                                output.accept(ModBlocks.ICE_ETHER_ORE);

                                output.accept(ModItems.ANTHRACITE);
                                output.accept(ModItems.ANTHRACITE2);

                                output.accept(ModItems.PROSPECTOR);

                                output.accept(ModItems.STRAWBERRY);
                                output.accept(ModItems.CHEESE);
                                output.accept(ModItems.CORN);

                                output.accept(ModBlocks.ICE_ETHER_STAIRS);
                                output.accept(ModBlocks.ICE_ETHER_SLAB);
                                output.accept(ModBlocks.ICE_ETHER_BUTTON);
                                output.accept(ModBlocks.ICE_ETHER_PRESSURE_PLATE);
                                output.accept(ModBlocks.ICE_ETHER_FENCE);
                                output.accept(ModBlocks.ICE_ETHER_FENCE_GATE);
                                output.accept(ModBlocks.ICE_ETHER_WALL);
                                output.accept(ModBlocks.ICE_ETHER_DOOR);
                                output.accept(ModBlocks.ICE_ETHER_TRAPDOOR);

                                output.accept(ModItems.FIRE_ETHER);
                                output.accept(ModItems.FIRE_ETHER_SWORD);
                                output.accept(ModItems.FIRE_ETHER_AXE);
                                output.accept(ModItems.FIRE_ETHER_PICKAXE);
                                output.accept(ModItems.FIRE_ETHER_SHOVEL);
                                output.accept(ModItems.FIRE_ETHER_HOE);

                                output.accept(ModItems.PICKAXE_AXE_ITEM);
                                output.accept(ModItems.PICKAXE_AXE_ITEM2);

                                output.accept(ModItems.ICE_ETHER_HELMET);
                                output.accept(ModItems.ICE_ETHER_CHESTPLATE);
                                output.accept(ModItems.ICE_ETHER_LEGGINGS);
                                output.accept(ModItems.ICE_ETHER_BOOTS);

                                output.accept(ModItems.STRAWBERRY_SEEDS);

                                output.accept(ModItems.FROST_SPIRIT_SPAWN_EGG);
                                output.accept(ModItems.FROST_BEE_SPAWN_EGG);

                            }).build());

    // 模组物品栏注册，需要在主类的构造方法中调用
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        ExampleMod.LOGGER.info("Registering ModCreativeModeTabs for " + ExampleMod.MOD_ID);
    }
}
