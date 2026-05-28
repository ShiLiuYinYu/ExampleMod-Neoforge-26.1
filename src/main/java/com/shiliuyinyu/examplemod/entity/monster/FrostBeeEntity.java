package com.shiliuyinyu.examplemod.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.server.level.ServerLevel;

/**
 * 冰霜蜜蜂 —— 飞行冰系敌对生物。
 * <p>
 * 继承 {@link Monster}，实现 {@link FlyingAnimal} 以支持飞行移动。
 * 具备飞行 AI、近战攻击并附带中毒效果、免疫摔落伤害。
 * 属性配置见 {@link #createAttributes()}，AI 配置见 {@link #registerGoals()}。
 */
public class FrostBeeEntity extends Monster implements FlyingAnimal {

    public FrostBeeEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(PathType.FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(false);
        return nav;
    }

    /**
     * 注册 AI 目标。
     * <p>
     * {@code goalSelector} 行为目标（优先级数字越小越高）：
     * <ol>
     *   <li>{@link FloatGoal} — 落入水中时上浮</li>
     *   <li>{@link MeleeAttackGoal} — 以 1.2 速度飞行近战攻击</li>
     *   <li>{@link WaterAvoidingRandomFlyingGoal} — 随机飞行（避开水域）</li>
     *   <li>{@link LookAtPlayerGoal} — 注视 8 格内的玩家</li>
     *   <li>{@link RandomLookAroundGoal} — 随机环顾四周</li>
     * </ol>
     * <p>
     * {@code targetSelector} 攻击目标：
     * <ol>
     *   <li>{@link NearestAttackableTargetGoal} — 主动攻击最近的玩家</li>
     * </ol>
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * 近战攻击命中时附加中毒效果（5 秒，I 级）。
     */
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (super.doHurtTarget(level, target)) {
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            }
            return true;
        }
        return false;
    }

    /**
     * 构建实体属性。
     * <ul>
     *   <li>最大生命值: 12（比普通僵尸低，蜜蜂体型小）</li>
     *   <li>移动速度: 0.3</li>
     *   <li>攻击伤害: 2.0（附带中毒弥补伤害）</li>
     *   <li>追踪范围: 24 格</li>
     *   <li>飞行速度: 0.6</li>
     * </ul>
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.FLYING_SPEED, 0.6D);
    }

    /** 飞行判定：不在地面时视为飞行状态 */
    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    /** 受伤音效：使用原版蜜蜂受伤音 */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BEE_HURT;
    }

    /** 死亡音效：使用原版蜜蜂死亡音 */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }

    /** 免疫摔落伤害 —— 飞行生物不应受重力影响 */
    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        return false;
    }
}
