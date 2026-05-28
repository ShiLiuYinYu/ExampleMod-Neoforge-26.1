package com.shiliuyinyu.examplemod.block;

import com.google.common.base.Supplier;
import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.block.custom.StrawberryCrop;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExampleMod.MOD_ID);

    // 用复制其他方块的属性的方法来创建方块
    // 可能会出现错误
    //public static final DeferredBlock<Block> ICE_ETHER_BLOCK = registerBlock("ice_ether_block",
    //  Block::new,()->BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), true);
    public static final DeferredBlock<Block> ICE_ETHER_BLOCK = registerBlock("ice_ether_block",
            properties -> new Block(properties
                    .strength(1.0f,3.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            )
            , true);
    public static final DeferredBlock<Block> RAW_ICE_ETHER_BLOCK = registerBlock("raw_ice_ether_block",
            properties -> new Block(properties.strength(1.0f,3.0f).requiresCorrectToolForDrops()), true);
    public static final DeferredBlock<Block> ICE_ETHER_ORE = registerBlock("ice_ether_ore",
            properties -> new Block(properties.strength(1.0f,3.0f).requiresCorrectToolForDrops()), true);


    //Stair 楼梯
    public static final DeferredBlock<StairBlock> ICE_ETHER_STAIRS = registerBlock("ice_ether_stairs",
            p->new StairBlock(ICE_ETHER_BLOCK.get().defaultBlockState(),p), ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()), true);
    //Slab 台阶
    public static final DeferredBlock<SlabBlock> ICE_ETHER_SLAB = registerBlock("ice_ether_slab",
            SlabBlock::new, ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()), true);
    //Button 按钮
    public static final DeferredBlock<ButtonBlock> ICE_ETHER_BUTTON = registerBlock("ice_ether_button",
            p->new ButtonBlock(BlockSetType.STONE, 20, p), ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()).noCollision(), true);
    //PressurePlate 压力板
    public static final DeferredBlock<PressurePlateBlock> ICE_ETHER_PRESSURE_PLATE = registerBlock("ice_ether_pressure_plate",
            p->new PressurePlateBlock(BlockSetType.STONE,p), ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()).noCollision(), true);
    //Fence 栅栏
    public static final DeferredBlock<FenceBlock> ICE_ETHER_FENCE = registerBlock("ice_ether_fence",
            FenceBlock::new, ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()), true);
    //FenceGate 栅栏门
    public static final DeferredBlock<FenceGateBlock> ICE_ETHER_FENCE_GATE = registerBlock("ice_ether_fence_gate",
            p->new FenceGateBlock(WoodType.ACACIA, p), ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()), true);
    //Wall 墙
    public static final DeferredBlock<WallBlock> ICE_ETHER_WALL = registerBlock("ice_ether_wall",
            WallBlock::new, ()->BlockBehaviour.Properties.ofFullCopy(ICE_ETHER_BLOCK.get()), true);
    //Door 门
    public static final DeferredBlock<DoorBlock> ICE_ETHER_DOOR = registerBlock("ice_ether_door",
            p->new DoorBlock(BlockSetType.IRON,p), ()->BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR), true);
    //TrapDoor 活板门
    public static final DeferredBlock<TrapDoorBlock> ICE_ETHER_TRAPDOOR = registerBlock("ice_ether_trapdoor",
            p->new TrapDoorBlock(BlockSetType.IRON,p), ()->BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_TRAPDOOR), true);

    //草莓作物方块
    public static final DeferredBlock<Block> STRAWBERRY_CROP = registerBlock("strawberry_crop",
            StrawberryCrop::new,()->BlockBehaviour.Properties.of()
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY),false
    );




    /**
     * 注册方块的完整方法（支持自定义方块属性）
     *
     * @param name 方块注册名称
     * @param func 方块构造函数字段，用于创建方块实例
     * @param properties 方块属性的提供者，用于配置方块行为
     * @param shouldRegisterItem 是否同时注册对应的方块物品
     * @return 已注册的延迟加载方块对象
     * @param <T> 方块类型，必须继承自Block类
     */
    private static <T extends Block>DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, Supplier<BlockBehaviour.Properties>  properties, boolean shouldRegisterItem){
        DeferredBlock<T> block = BLOCKS.registerBlock(name, func, properties);
        if(shouldRegisterItem){
            ModItems.ITEMS.registerSimpleBlockItem(block);
        }
        return block;
    }

    /**
     * 注册方块的简化方法（使用默认方块属性）
     *
     * @param name 方块注册名称
     * @param func 方块构造函数字段，用于创建方块实例
     * @param shouldRegisterItem 是否同时注册对应的方块物品
     * @return 已注册的延迟加载方块对象
     * @param <T> 方块类型，必须继承自Block类
     */
    private static <T extends Block>DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, boolean shouldRegisterItem){
        DeferredBlock<T> block = BLOCKS.registerBlock(name, func);
        if(shouldRegisterItem){
            ModItems.ITEMS.registerSimpleBlockItem(block);
        }
        return block;
    }

    // 注册方块，需要在主类的构造方法中调用
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ExampleMod.LOGGER.info("Registering ModBlocks for " + ExampleMod.MOD_ID);
    }

}
