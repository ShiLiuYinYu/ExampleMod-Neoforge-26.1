# Mod 生物实体创建指南

> NeoForge 26.1 (Minecraft 26.1) · 基于原版 Mojang 命名约定

---

## 目录

1. [概述](#1-概述)
2. [目录结构约定](#2-目录结构约定)
3. [EntityType 注册](#3-entitytype-注册)
4. [实体类编写](#4-实体类编写)
5. [AI 系统](#5-ai-系统)
6. [属性注册](#6-属性注册)
7. [模型系统](#7-模型系统)
8. [渲染器](#8-渲染器)
9. [客户端注册](#9-客户端注册)
10. [刷怪蛋](#10-刷怪蛋)
11. [数据生成](#11-数据生成)
12. [MC 26.1 关键 API 变化](#12-mc-261-关键-api-变化)
13. [完整代码清单](#13-完整代码清单)
14. [调试与验证](#14-调试与验证)

---

## 1. 概述

在 NeoForge 26.1 中创建一个自定义生物实体，需要完成以下步骤：

- 创建实体类（继承合适的基类）
- 注册 `EntityType`
- 注册实体属性
- 创建模型和渲染器
- 客户端侧注册渲染器
- （可选）创建刷怪蛋
- （可选）数据生成：战利品表、语言文件

### 实体继承体系

```
Entity                          ─ 所有实体的基类
├── LivingEntity                ─ 有生命的实体（HP、状态效果）
│   ├── Player                  ─ 玩家
│   └── Mob                     ─ 可移动的生物（AI）
│       ├── PathfinderMob       ─ 有寻路的生物
│       │   ├── Monster         ─ 敌对生物（僵尸、骷髅...）
│       │   ├── Animal          ─ 被动动物
│       │   └── AmbientCreature ─ 环境生物（蝙蝠）
│       └── AgeableMob          ─ 可成长的生物
│           └── TamableAnimal   ─ 可驯服的动物
├── Projectile                  ─ 弹射物
└── ItemEntity                  ─ 掉落物
```

**选择基类指南：**

| 生物类型 | 推荐基类 | 示例 |
|---|---|---|
| 敌对怪物 | `Monster` | 僵尸、骷髅、苦力怕 |
| 被动动物 | `Animal` | 牛、羊、猪 |
| 可驯服动物 | `TamableAnimal` | 狼、猫 |
| 环境生物 | `AmbientCreature` | 蝙蝠 |
| 飞行生物 | `Mob`/`Animal` + 接口 `FlyingAnimal` | 蜜蜂→Animal、恶魂→Mob、蝙蝠→AmbientCreature |
| 水生生物 | `WaterAnimal` | 鱿鱼、鱼 |

---

## 2. 目录结构约定

按照 Minecraft 原版的命名标准，mod 实体代码按以下结构组织：

```
src/main/java/<mod_package>/
├── entity/
│   ├── ModEntities.java           # 实体类型注册中心（相当于原版 EntityType.java）
│   ├── ModEntityAttributes.java   # 实体属性注册（监听 EntityAttributeCreationEvent）
│   └── monster/                   # 按行为类型分包
│       └── FrostSpiritEntity.java # 具体实体类
│   └── animal/                    # 未来可扩展：被动动物
│   └── projectile/                # 未来可扩展：弹射物
│
├── client/
│   ├── model/
│   │   └── monster/               # 模型按实体类型分包
│   │       └── FrostSpiritModel.java
│   │   └── animal/                # 未来可扩展
│   │
│   └── renderer/
│       └── entity/                # 渲染器统一放 entity/ 子包下
│           └── FrostSpiritRenderer.java
│
├── datagen/                       # 数据生成
│   └── ModEntityLootTablesProvider.java
│
└── item/
    └── ModItems.java              # 物品注册（含刷怪蛋）
```

### 与原版的对应关系

| 原版路径 | Mod 对应路径 | 用途 |
|---|---|---|
| `net/minecraft/world/entity/EntityType.java` | `<mod_pkg>/entity/ModEntities.java` | 实体类型注册 |
| `net/minecraft/world/entity/monster/Monster.java` | `<mod_pkg>/entity/monster/` | 怪物实体类 |
| `net/minecraft/client/model/monster/` | `<mod_pkg>/client/model/monster/` | 怪物模型 |
| `net/minecraft/client/renderer/entity/` | `<mod_pkg>/client/renderer/entity/` | 实体渲染器 |

---

## 3. EntityType 注册

### 3.1 DeferredRegister 模式

创建 `ModEntities.java`，使用 NeoForge 的 `DeferredRegister` 系统：

```java
package com.shiliuyinyu.examplemod.entity;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.entity.monster.FrostSpiritEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ExampleMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<FrostSpiritEntity>> FROST_SPIRIT =
            ENTITY_TYPES.register("frost_spirit",
                    () -> EntityType.Builder.of(FrostSpiritEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 0.8F)          // 碰撞箱宽度、高度
                            .eyeHeight(0.7F)             // 眼睛高度
                            .clientTrackingRange(8)       // 客户端追踪范围
                            .updateInterval(20)           // 更新间隔（tick）
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_spirit"))));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
```

### 3.2 EntityType.Builder 参数说明

| 方法 | 说明 | 默认值 |
|---|---|---|
| `.of(factory, category)` | 实体构造工厂 + 分类 | **必填** |
| `.sized(w, h)` | 碰撞箱宽度和高度（格） | **必填** |
| `.eyeHeight(h)` | 眼睛高度（格），影响第一人称视角 | 0.0 |
| `.clientTrackingRange(n)` | 客户端追踪范围（格），超出此范围实体不渲染 | 5 |
| `.updateInterval(n)` | 服务端更新间隔（tick），越低越频繁 | 3 |
| `.fireImmune()` | 免疫火焰伤害 | false |
| `.noLootTable()` | 无战利品表 | false |
| `.noSummon()` | 禁止 /summon | false |
| `.noSave()` | 不保存到磁盘 | false |
| `.spawnDimensions(predicate)` | 限制生成维度 | 全部维度 |

### 3.3 MobCategory 对照表

| 分类 | 说明 | 和平模式 |
|---|---|---|
| `MobCategory.MONSTER` | 敌对怪物 | 不生成 |
| `MobCategory.CREATURE` | 被动动物 | 正常生成 |
| `MobCategory.AMBIENT` | 环境生物 | 正常生成 |
| `MobCategory.WATER_CREATURE` | 水生生物 | 正常生成 |
| `MobCategory.MISC` | 杂项（物品实体、经验球） | 正常 |

### 3.4 在主类中调用注册

在 `ExampleMod.java` 构造函数中添加：

```java
ModEntities.register(modEventBus);
```

---

## 4. 实体类编写

### 4.1 基本结构

创建一个继承 `Monster` 的敌对生物类：

```java
package com.shiliuyinyu.examplemod.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class FrostSpiritEntity extends Monster {

    public FrostSpiritEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
}
```

### 4.2 必须 Override 的方法

| 方法 | 说明 |
|---|---|
| `registerGoals()` | 注册 AI 目标 |
| 静态 `createAttributes()` | 创建属性构建器 |

### 4.3 常用可 Override 的钩子方法

| 方法 | 说明 |
|---|---|
| `getHurtSound(DamageSource)` | 受伤音效 |
| `getDeathSound()` | 死亡音效 |
| `getAmbientSound()` | 环境音效（闲置时播放） |
| `causeFallDamage(distance, modifier, source)` | 摔落伤害，返回 `false` 免疫 |
| `isSunSensitive()` | 阳光下是否燃烧（亡灵生物） |
| `aiStep()` | 每 tick 调用的 AI 逻辑 |
| `tick()` | 每 tick 调用 |
| `canBeLeashed()` | 是否可用拴绳 |

---

## 5. AI 系统

### 5.1 GoalSelector 与 TargetSelector

Minecraft 使用两个选择器管理 AI：

- **`goalSelector`** — 行为目标（移动、攻击、躲避等）
- **`targetSelector`** — 目标选择（选择攻击目标）

优先级数字越小，优先级越高。

### 5.2 常用 AI Goal 速查表

**行为类 Goal（goalSelector）：**

| Goal 类 | 优先级建议 | 说明 |
|---|---|---|
| `FloatGoal` | 1 | 在水中上浮 |
| `PanicGoal` | 1 | 受伤后逃跑 |
| `MeleeAttackGoal` | 2 | 近战攻击 |
| `RangedAttackGoal` | 2 | 远程攻击 |
| `WaterAvoidingRandomStrollGoal` | 3 | 随机移动（避开水中） |
| `RandomStrollGoal` | 3 | 完全随机移动 |
| `LookAtPlayerGoal` | 4 | 注视玩家 |
| `RandomLookAroundGoal` | 4 | 随机环顾 |
| `LeapAtTargetGoal` | 3 | 跳跃攻击 |
| `MoveTowardsRestrictionGoal` | 3 | 返回活动范围 |
| `FleeSunGoal` | 2 | 躲避阳光（亡灵） |
| `RestrictSunGoal` | 2 | 白天躲在阴凉处 |
| `BreakDoorGoal` | 1 | 破门 |
| `TemptGoal` | 3 | 被食物吸引 |

**目标选择 Goal（targetSelector）：**

| Goal 类 | 说明 |
|---|---|
| `NearestAttackableTargetGoal<Player>` | 攻击最近的玩家 |
| `NearestAttackableTargetGoal<IronGolem>` | 攻击最近的铁傀儡 |
| `HurtByTargetGoal` | 反击攻击者 |
| `OwnerHurtByTargetGoal` | 主人被攻击时反击（驯服生物） |
| `OwnerHurtTargetGoal` | 帮助主人攻击目标（驯服生物） |
| `ResetUniversalAngerTargetGoal` | 中立生物愤怒机制 |

### 5.3 完整 AI 示例

```java
@Override
protected void registerGoals() {
    // 优先级1：生存（游泳）
    this.goalSelector.addGoal(1, new FloatGoal(this));

    // 优先级2：战斗（近战攻击）
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

    // 优先级3：移动（随机漫步，避开水中）
    this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));

    // 优先级4：视觉（注视玩家、环顾）
    this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

    // 目标选择：攻击最近的玩家
    this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
}
```

---

## 6. 属性注册

### 6.1 定义属性

在实体类中创建静态工厂方法：

```java
public static AttributeSupplier.Builder createAttributes() {
    return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)        // 最大生命值
            .add(Attributes.MOVEMENT_SPEED, 0.25D)     // 移动速度
            .add(Attributes.ATTACK_DAMAGE, 3.0D)       // 攻击伤害
            .add(Attributes.ATTACK_SPEED, 1.0D)        // 攻击速度
            .add(Attributes.FOLLOW_RANGE, 35.0D)       // 追踪范围
            .add(Attributes.ARMOR, 0.0D)               // 护甲
            .add(Attributes.ARMOR_TOUGHNESS, 0.0D)     // 护甲韧性
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D); // 击退抗性
}
```

### 6.2 常用属性参考值

| 属性 | 典型值 | 说明 |
|---|---|---|
| `MAX_HEALTH` | 20.0（玩家） | 僵尸20、骷髅20、苦力怕20、末影人40 |
| `MOVEMENT_SPEED` | 0.25 | 僵尸0.23、骷髅0.25、蜘蛛0.3 |
| `ATTACK_DAMAGE` | 2.0~6.0 | 僵尸3.0、铁傀儡7.0~21.0 |
| `FOLLOW_RANGE` | 35.0 | 标准追踪距离 |
| `ATTACK_KNOCKBACK` | 0.0 | 击退强度 |
| `FLYING_SPEED` | 0.4 | 飞行速度（飞行生物） |

### 6.3 注册属性

创建 `ModEntityAttributes.java` 使用 `EntityAttributeCreationEvent`：

```java
@EventBusSubscriber(modid = ExampleMod.MOD_ID)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FROST_SPIRIT.get(), FrostSpiritEntity.createAttributes().build());
    }
}
```

---

## 7. 模型系统

### 7.1 MC 26.1 模型架构

MC 26.1 引入了 **RenderState** 系统。模型不再直接接收 Entity，而是接收 `EntityRenderState`。

```java
public class FrostSpiritModel extends EntityModel<LivingEntityRenderState> {
    // ...
}
```

### 7.2 模型类结构

```java
package com.shiliuyinyu.examplemod.client.model.monster;

import com.shiliuyinyu.examplemod.ExampleMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class FrostSpiritModel extends EntityModel<LivingEntityRenderState> {

    // 1. 定义 LAYER_LOCATION — 注册模型层时使用
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_spirit"),
                "main");

    // 2. 声明模型部件
    private final ModelPart head;
    private final ModelPart body;
    // ...

    // 3. 构造函数 — 接收 root ModelPart
    public FrostSpiritModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        // ...
    }

    // 4. 静态工厂 — 创建 LayerDefinition（BlockBench 导出的部分）
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();
        // ... 添加部件 ...
        return LayerDefinition.create(meshdefinition, textureWidth, textureHeight);
    }

    // 5. setupAnim — 动画逻辑（接收 RenderState 而非 Entity）
    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * (float)(Math.PI / 180.0);
        this.head.xRot = state.xRot * (float)(Math.PI / 180.0);
    }
}
```

### 7.3 BlockBench 导出流程

1. 在 **BlockBench** 中创建 "Modded Entity" 项目
2. 模型根节点命名为实体名（如 `frost_spirit`）
3. 纹理尺寸建议 64×32（标准）或 64×64（高清）
4. 导出：**File → Export → Export Java Entity**
5. 将 `createBodyLayer()` 方法的内容复制到模型类中
6. 修改构造函数中的 `getChild()` 调用，确保名字与 BlockBench 中的部件名一致

### 7.4 纹理位置

```
src/main/resources/assets/<mod_id>/textures/entity/<entity_name>.png
```

示例：`assets/example/textures/entity/frost_spirit.png`

---

## 8. 渲染器

### 8.1 MC 26.1 渲染器结构

渲染器使用新的三参数泛型：`<Entity, RenderState, Model>`

```java
package com.shiliuyinyu.examplemod.client.renderer.entity;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.client.model.monster.FrostSpiritModel;
import com.shiliuyinyu.examplemod.entity.monster.FrostSpiritEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class FrostSpiritRenderer
        extends MobRenderer<FrostSpiritEntity, LivingEntityRenderState, FrostSpiritModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/entity/frost_spirit.png");

    public FrostSpiritRenderer(EntityRendererProvider.Context context) {
        super(context,
              new FrostSpiritModel(context.bakeLayer(FrostSpiritModel.LAYER_LOCATION)),
              0.5F);  // 阴影半径
    }

    // MC 26.1 新增：必须创建 RenderState
    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
```

### 8.2 渲染器基类选择

| 基类 | 适用场景 |
|---|---|
| `EntityRenderer<T, S>` | 所有实体的抽象基类 |
| `MobRenderer<T, S, M>` | 标准生物（有模型+纹理） |
| `LivingEntityRenderer<T, S, M>` | 有生命实体（含血量条等UI） |
| `HumanoidMobRenderer<T, S, M>` | 人形生物（僵尸、村民等） |
| `AgeableMobRenderer<T, S, M>` | 有幼年形态的生物 |
| `ThrownItemRenderer<T>` | 投掷物（雪球、末影珍珠等） |

---

## 9. 客户端注册

### 9.1 在 ExampleModClient 中注册

```java
@Mod(value = ExampleMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ExampleMod.MOD_ID, value = Dist.CLIENT)
public class ExampleModClient {

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FROST_SPIRIT.get(), FrostSpiritRenderer::new);
    }

    @SubscribeEvent
    static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FrostSpiritModel.LAYER_LOCATION, FrostSpiritModel::createBodyLayer);
    }
}
```

### 9.2 注册时机说明

| 事件 | 触发时机 | 用途 |
|---|---|---|
| `RegisterRenderers` | 客户端启动，注册渲染器 | 绑定 `EntityType` → `EntityRenderer` |
| `RegisterLayerDefinitions` | 客户端启动，注册模型层 | 绑定 `ModelLayerLocation` → `LayerDefinition` |

---

## 10. 刷怪蛋

### 10.1 MC 26.1 新方式

MC 26.1 中刷怪蛋不再通过构造函数传入 `EntityType` 和颜色。改为使用 **DataComponents.ENTITY_DATA** 数据组件。

```java
// 在 ModItems.java 中：
public static final DeferredItem<Item> FROST_SPIRIT_SPAWN_EGG =
        ITEMS.registerItem("frost_spirit_spawn_egg",
                p -> new SpawnEggItem(
                        p.useItemDescriptionPrefix()
                         .component(DataComponents.ENTITY_DATA,
                                    TypedEntityData.of(ModEntities.FROST_SPIRIT.get(), new CompoundTag()))));
```

**关键要点：**
- 必须使用 `SpawnEggItem` 类（提供右键生成实体的逻辑）
- `ENTITY_DATA` 组件包含实体的 `EntityType` 引用
- 需要使用 `TypedEntityData.of(entityType, new CompoundTag())` 创建数据
- 刷怪蛋的颜色由模型/纹理系统渲染，不再由代码指定

### 10.2 将刷怪蛋加入创造模式标签页

```java
// 在 ExampleMod.java 的 addCreative 方法中：
if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
    event.accept(ModItems.FROST_SPIRIT_SPAWN_EGG.get());
}
```

---

## 11. 数据生成

### 11.1 实体战利品表

创建 `ModEntityLootTablesProvider.java`：

```java
package com.shiliuyinyu.examplemod.datagen;

import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

public class ModEntityLootTablesProvider extends EntityLootSubProvider {

    public ModEntityLootTablesProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    public void generate() {
        this.add(ModEntities.FROST_SPIRIT.get(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(ModItems.ICE_ETHER.get())
                                        .apply(SetItemCountFunction.setCount(
                                                UniformGenerator.between(1.0F, 3.0F)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                                this.registries,
                                                UniformGenerator.between(0.0F, 1.0F))))));
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.ENTITY_TYPES.getEntries().stream()
                .map(holder -> (EntityType<?>) holder.value());
    }
}
```

### 11.2 注册战利品表提供者

在 `ExampleModDataGenerator.java` 中：

```java
event.createProvider(((output, lookupProvider) ->
        new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(
                        ModBlockLootTablesProvider::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(
                        ModEntityLootTablesProvider::new, LootContextParamSets.ENTITY)
        ), lookupProvider)));
```

### 11.3 语言文件

在 `ModEnUsLangProvider.java` 中：

```java
add(ModEntities.FROST_SPIRIT.get(), "Frost Spirit");
add(ModItems.FROST_SPIRIT_SPAWN_EGG.get(), "Frost Spirit Spawn Egg");
```

### 11.4 刷怪蛋物品模型

在 `ModModelsProvider.java` 中：

```java
itemModels.generateFlatItem(ModItems.FROST_SPIRIT_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
```

### 11.5 运行数据生成

```bash
./gradlew runData
```

生成的文件位于 `src/generated/resources/` 目录下。

---

## 12. MC 26.1 关键 API 变化

对比旧版 Minecraft / NeoForge，MC 26.1 有以下重要变化：

### 12.1 ResourceLocation → Identifier

```java
// 旧：net.minecraft.resources.ResourceLocation
// 新：net.minecraft.resources.Identifier

// 旧写法
new ResourceLocation("example", "frost_spirit")

// 新写法
Identifier.fromNamespaceAndPath("example", "frost_spirit")
Identifier.withDefaultNamespace("frost_spirit")  // 等同于 minecraft:frost_spirit
```

### 12.2 RenderState 渲染系统

模型和渲染器不再直接使用 Entity 对象，而是使用 RenderState：

```java
// 旧：模型直接接收实体
public class MyModel extends EntityModel<MyEntity> { ... }

// 新：模型接收 RenderState
public class MyModel extends EntityModel<LivingEntityRenderState> { ... }

// 渲染器必须实现 createRenderState()
@Override
public LivingEntityRenderState createRenderState() {
    return new LivingEntityRenderState();
}

// getTextureLocation 接收 RenderState 而非 Entity
@Override
public Identifier getTextureLocation(LivingEntityRenderState state) { ... }
```

### 12.3 EntityType.Builder.build() 参数变化

```java
// 旧：build() 接收 String
.build("frost_spirit")

// 新：build() 接收 ResourceKey<EntityType<?>>
.build(ResourceKey.create(Registries.ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_spirit")))
```

### 12.4 SpawnEggItem 构造方式

```java
// 旧：构造函数接收 EntityType + 颜色
new SpawnEggItem(entityType, 0x8FDFFF, 0x3FA8C8, properties)

// 新：使用 DataComponents.ENTITY_DATA 组件
new SpawnEggItem(properties.component(
    DataComponents.ENTITY_DATA,
    TypedEntityData.of(entityType, new CompoundTag())))
```

### 12.5 @EventBusSubscriber bus 参数移除

```java
// 旧：需要指定 bus
@EventBusSubscriber(modid = "example", bus = EventBusSubscriber.Bus.MOD)

// 新：不需要 bus 参数（默认 MOD 总线）
@EventBusSubscriber(modid = "example")
```

### 12.6 实体免疫摔落伤害

```java
// 旧：checkFallDamage(y, onGround) 返回 boolean
@Override
public boolean checkFallDamage(double y, boolean onGround) { return false; }

// 新：causeFallDamage(distance, modifier, source) 返回 boolean
@Override
public boolean causeFallDamage(double distance, float modifier, DamageSource source) {
    return false;
}
```

---

## 13. 完整代码清单

### 13.1 目录树

```
src/main/java/com/shiliuyinyu/examplemod/
├── ExampleMod.java                      # 主类：注册 ModEntities
├── ExampleModClient.java                # 客户端：注册渲染器和模型层
├── ExampleModDataGenerator.java         # 数据生成入口
├── Config.java
├── block/
│   ├── ModBlocks.java
│   └── custom/
│       └── StrawberryCrop.java
├── client/
│   ├── model/
│   │   └── monster/
│   │       └── FrostSpiritModel.java    # 实体模型
│   └── renderer/
│       └── entity/
│           └── FrostSpiritRenderer.java # 实体渲染器
├── datagen/
│   ├── ModBlockLootTablesProvider.java
│   ├── ModBlockTagsProvider.java
│   ├── ModDataMapsProvider.java
│   ├── ModEnUsLangProvider.java
│   ├── ModEntityLootTablesProvider.java # 实体战利品表
│   ├── ModEquipmentAssetsProvider.java
│   ├── ModItemTagsProvider.java
│   ├── ModModelsProvider.java
│   └── ModRecipesProvider.java
├── entity/
│   ├── ModEntities.java                 # EntityType 注册中心
│   ├── ModEntityAttributes.java         # 属性注册
│   └── monster/
│       └── FrostSpiritEntity.java       # 实体类
├── item/
│   ├── ModArmorMaterials.java
│   ├── ModConsumables.java
│   ├── ModCreativeModeTabs.java
│   ├── ModFoods.java
│   ├── ModItems.java
│   ├── ModToolMaterials.java
│   └── custom/
│       ├── ModFuelItem.java
│       ├── PickaxeAxeItem.java
│       └── ProspectorItem.java
├── tag/
│   ├── ModBlockTags.java
│   └── ModItemTags.java
└── util/
    └── ModArmorEffects.java

src/main/resources/
├── assets/example/
│   └── textures/entity/
│       └── frost_spirit.png            # 实体纹理（64×32）

src/generated/resources/                 # 运行 runData 后生成
├── assets/example/
│   ├── items/frost_spirit_spawn_egg.json
│   └── lang/en_us.json
└── data/example/loot_table/entities/
    └── frost_spirit.json               # 战利品表
```

### 13.2 文件清单速查

| 文件 | 行数 | 作用 |
|---|---|---|
| `ModEntities.java` | ~30 | EntityType 注册 |
| `FrostSpiritEntity.java` | ~55 | 实体类（AI、属性、音效） |
| `ModEntityAttributes.java` | ~15 | 属性绑定 |
| `FrostSpiritModel.java` | ~65 | 实体模型（64×32） |
| `FrostSpiritRenderer.java` | ~30 | 实体渲染器 |
| `ModEntityLootTablesProvider.java` | ~45 | 战利品表数据生成 |
| `ExampleMod.java` | ~95 | 主类（需加入 ModEntities.register） |
| `ExampleModClient.java` | ~45 | 客户端（需加入渲染器注册） |
| `ModItems.java` | ~80 | 物品注册（需加入刷怪蛋） |

---

## 14. 调试与验证

### 14.1 构建验证

```bash
# 编译
./gradlew compileJava

# 运行数据生成
./gradlew runData

# 完整打包
./gradlew jar
```

### 14.2 游戏内验证

启动客户端后：
```bash
./gradlew runClient
```

在游戏中使用以下命令测试：

| 命令 | 用途 |
|---|---|
| `/summon example:frost_spirit ~ ~ ~` | 在玩家位置召唤实体 |
| `/summon example:frost_spirit ~ ~1 ~ {NoAI:1b}` | 召唤无 AI 的实体（检查模型） |
| `/kill @e[type=example:frost_spirit]` | 清除所有 FrostSpirit |
| `/effect give @e[type=example:frost_spirit] minecraft:glowing` | 给实体发光效果（检查碰撞箱） |

### 14.3 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|---|---|---|
| 实体不渲染（透明） | 渲染器未注册 | 检查 `ExampleModClient` 中 `registerRenderers` |
| 模型全是白色 | 纹理路径错误 | 检查 `getTextureLocation` 返回的 `Identifier` |
| 实体不动 | AI Goal 未注册 | 检查 `registerGoals()` 是否被调用 |
| `/summon` 找不到实体 | EntityType 未注册 | 检查 `ModEntities.register()` 是否在构造函数中调用 |
| 刷怪蛋无法使用 | ENTITY_DATA 组件未设置 | 检查 `TypedEntityData.of()` 调用 |
| 掉落物为空 | 战利品表未生成 | 运行 `./gradlew runData` |
| 实体名称显示 `entity.example.frost_spirit` | 语言文件缺失 | 运行 `./gradlew runData` 生成 en_us.json |
| 游戏崩溃：`NoSuchMethodError` | API 版本不匹配 | 确认使用 MC 26.1 的 API（见第12节变化） |
| 模型部位错位 | BlockBench 导出部件名不一致 | 确保 `getChild("name")` 与 BlockBench 中命名一致 |

---

## 附录：开发流程总结

```
1. 创建实体类        entity/monster/YourEntity.java
2. 注册 EntityType    entity/ModEntities.java
3. 注册属性           entity/ModEntityAttributes.java
4. 创建模型           client/model/monster/YourModel.java
5. 创建渲染器         client/renderer/entity/YourRenderer.java
6. 客户端注册         ExampleModClient.java
7. 创建刷怪蛋         item/ModItems.java
8. 数据生成           datagen/ModEntityLootTablesProvider.java 等
9. 运行 runData       ./gradlew runData
10. 测试              ./gradlew runClient → /summon 命令
```
