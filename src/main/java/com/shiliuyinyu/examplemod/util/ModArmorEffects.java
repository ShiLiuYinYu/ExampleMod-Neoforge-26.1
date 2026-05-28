package com.shiliuyinyu.examplemod.util;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.tag.ModItemTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ExampleMod.MOD_ID)
public class ModArmorEffects {

    public static final Map<TagKey<Item>, List<Template>> EFFECTS =
        Map.of(ModItemTags.ICE_ETHER_ARMOR, List.of(
                new Template(MobEffects.SPEED, 200, 1),
                new Template(MobEffects.JUMP_BOOST, 200, 1)
        ));

    private static boolean hasFullArmor(Player player, TagKey<Item> tag){
        return  player.getItemBySlot(EquipmentSlot.HEAD).is(tag) &&
                player.getItemBySlot(EquipmentSlot.CHEST).is(tag) &&
                player.getItemBySlot(EquipmentSlot.LEGS).is(tag) &&
                player.getItemBySlot(EquipmentSlot.FEET).is(tag);
    }

    private static void tickPlayer(Player player){
        for(var entry : EFFECTS.entrySet()){
            if(hasFullArmor(player, entry.getKey())){
                applyEffects(player, entry.getValue());
                break;
            }
        }
    }

    private static void applyEffects(Player player, List<Template> effects){
        for(var template : effects){
            MobEffectInstance effectinstance = new MobEffectInstance(template.effect(), template.duration(), template.amplifier(), false, false, true);
            if(!player.hasEffect(effectinstance.getEffect())){
                player.addEffect(effectinstance);
            }
        }
    }

    @SubscribeEvent
    public static void register(LevelTickEvent.Post event){
        for(Player player : event.getLevel().players()){
            tickPlayer(player);
        }
    }

    public record Template(Holder<MobEffect> effect, int duration, int amplifier){

    }
}
