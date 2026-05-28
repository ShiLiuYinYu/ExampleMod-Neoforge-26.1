package com.shiliuyinyu.examplemod.tag;


import com.shiliuyinyu.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> ORE_TAGS = create("ore_tags");
    public static final TagKey<Block> PICKAXE_AXE_MINEABLE = create("pickaxe_axe_mineable");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, name));
    }

}
