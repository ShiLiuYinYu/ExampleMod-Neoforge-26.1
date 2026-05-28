package com.shiliuyinyu.examplemod.item.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 探矿杖 —— 右键点击方块时向下搜索矿物。
 *
 * 工作原理：
 * - 右键方块时触发 {@link #useOn(UseOnContext)}，仅在服务端执行搜索逻辑。
 * - 未潜行：以点击位置为中心，向下最多搜索 64 格，在 5×5 的截面区域内逐层探测。
 * - 潜行时：仅沿点击位置垂直向下搜索，用于精确定位。
 * - 找到符合 {@link #isCorrectBlock(BlockState)} 的矿物时向玩家发送坐标消息，
 *   如果整个搜索范围都没有矿物则提示 "No ore found"。
 * - 每次使用消耗 1 点耐久。
 */
public class ProspectorItem extends Item {
    public ProspectorItem(Properties properties) {
        super(properties);
    }

    /**
     * 右键使用逻辑。仅在服务端执行：根据玩家是否潜行选择搜索模式，
     * 向玩家报告搜索范围内找到的矿物坐标，若未找到则提示 "No ore found"，
     * 最后消耗 1 点物品耐久。
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if(!level.isClientSide()){
            boolean found = false;
            // 未潜行：5×5 区域扫描，从点击位置向下最多 64 格逐层搜索
            if(!Minecraft.getInstance().hasShiftDown()){
                for(int i = 0; i <= blockPos.getY()+64; i++){
                    for(int j = 0; j < 5; j++){
                        for(int k = 0; k < 5; k++){
                            // 在点击位置下方 i 格处，以 (北 j, 东 k) 偏移构成 5×5 扫描网格
                            BlockPos pos1 = blockPos.below(i).north(j).east(k);
                            BlockState blockState = level.getBlockState(pos1);
                            String name = blockState.getBlock().getName().getString();

                            if(isCorrectBlock(blockState)){
                                player.sendSystemMessage(Component.literal("Found " + name + " at " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()));
                                found = true;
                                break;
                            }

                        }
                    }
                }
            }else{
                // 潜行时：仅垂直向下单列扫描，精确定位矿物所在深度
                for(int i = 0; i<= blockPos.getY() + 64; i++){
                    BlockPos pos1 = blockPos.below(i);
                    BlockState blockState = level.getBlockState(pos1);
                    String name = blockState.getBlock().getName().getString();

                    if(isCorrectBlock(blockState)){
                        player.sendSystemMessage(Component.literal("Found " + name + " at " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()));
                        found = true;
                        break;
                    }

                }
            }

            if(!found){
                player.sendSystemMessage(Component.literal("No ore found"));
            }

            context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * 判定目标方块是否为矿物。支持的矿物包括：钻石矿、铁矿、煤矿、金矿、
     * 青金石矿、红石矿、绿宝石矿、下界金矿、铜矿。
     */
    private boolean isCorrectBlock(BlockState blockState){
        return blockState.is(Blocks.DIAMOND_ORE) || blockState.is(Blocks.IRON_ORE) || blockState.is(Blocks.COAL_ORE) || blockState.is(Blocks.GOLD_ORE) || blockState.is(Blocks.LAPIS_ORE) || blockState.is(Blocks.REDSTONE_ORE) || blockState.is(Blocks.EMERALD_ORE) || blockState.is(Blocks.NETHER_GOLD_ORE) || blockState.is(Blocks.COPPER_ORE);
    }


}
