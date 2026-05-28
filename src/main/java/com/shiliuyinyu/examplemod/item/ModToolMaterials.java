package com.shiliuyinyu.examplemod.item;

import com.shiliuyinyu.examplemod.tag.ModItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public class ModToolMaterials {


    /**
    * 自定义工具材质,一共6个字段
     * 1. 不可破坏的方块标签(挖掘等级)
     * 2. 耐久度
     * 3. 挖掘速度
     * 4. 攻击伤害
     * 5. 附魔值
     * 6. 维修物品(可以是标签)
    * */
    public static final ToolMaterial FIRE_ETHER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            500,
            10.0f,
            5.0f,
            2,
            ModItemTags.FIRE_ETHER_TOOL_MATERIALS
    );
}
