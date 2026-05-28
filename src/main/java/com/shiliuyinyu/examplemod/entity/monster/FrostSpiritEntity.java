package com.shiliuyinyu.examplemod.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * 冰霜精灵 —— 浮空冰系敌对生物。
 * <p>
 * 继承 {@link Monster}，拥有近战攻击 AI，免疫摔落伤害。
 * 属性配置见 {@link #createAttributes()}，AI 配置见 {@link #registerGoals()}。
 */
public class FrostSpiritEntity extends Monster {

    public FrostSpiritEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    /**
     * 注册 AI 目标。
     * <p>
     * {@code goalSelector} 管理行为目标（优先级数字越小越高）：
     * <ol>
     *   <li>{@link FloatGoal} — 落入水中时上浮</li>
     *   <li>{@link MeleeAttackGoal} — 以 1.0 速度近战攻击</li>
     *   <li>{@link WaterAvoidingRandomStrollGoal} — 随机漫步（避开水域）</li>
     *   <li>{@link LookAtPlayerGoal} — 注视 8 格内的玩家</li>
     *   <li>{@link RandomLookAroundGoal} — 随机环顾四周</li>
     * </ol>
     * <p>
     * {@code targetSelector} 管理攻击目标：
     * <ol>
     *   <li>{@link NearestAttackableTargetGoal} — 主动攻击最近的玩家</li>
     * </ol>
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * 构建实体属性。
     * <ul>
     *   <li>最大生命值: 20（等同于玩家）</li>
     *   <li>移动速度: 0.25（等同于僵尸/骷髅）</li>
     *   <li>攻击伤害: 3.0（等同于普通僵尸）</li>
     *   <li>追踪范围: 35 格</li>
     * </ul>
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D);
    }

    /** 受伤音效：使用原版通用敌对生物受伤音 */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.HOSTILE_HURT;
    }

    /** 死亡音效：使用原版通用敌对生物死亡音 */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }

    /** 免疫摔落伤害 —— 浮空生物不应受重力影响 */
    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        return false;
    }

    /**
     * FrostSpirit 生成规则谓词：仅允许在寒冷群系（温度 &lt; 0.25）且满足原版怪物生成光照条件时生成。
     * 参考 {@link Monster#checkMonsterSpawnRules}。
     */
    public static boolean checkSpawnRules(EntityType<FrostSpiritEntity> type, ServerLevelAccessor level,
                                                      EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getBiome(pos).value().getBaseTemperature() < 0.25F
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }
}
