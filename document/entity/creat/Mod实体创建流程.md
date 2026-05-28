# Mod 实体创建流程

> NeoForge 26.1 · 从零创建一个自定义生物的标准步骤

---

## 流程图

```
选目录 → 实体类 → EntityType注册 → 属性注册 → 模型 → 渲染器 → 客户端注册 → 刷怪蛋 → 数据生成 → 测试
```

每步只需几行代码，遵循约定好的目录结构和注册模式即可。

---

## 第 1 步：确定实体类型，选目录

根据实体行为选择子包，对齐原版 Mojang 标准：

| 生物类型 | 实体类目录 | 模型目录 |
|---------|-----------|----------|
| 敌对怪物 | `entity/monster/` | `client/model/monster/` |
| 被动动物 | `entity/animal/` | `client/model/animal/` |
| 弹射物 | `entity/projectile/` | 通常不需要独立模型 |

渲染器统一放 `client/renderer/entity/`，不按类型再拆分。

---

## 第 2 步：新建实体类

在对应目录创建类，继承合适的基类：

| 基类 | 适用场景 |
|-----|---------|
| `Monster` | 敌对怪物（僵尸、骷髅类） |
| `Animal` | 被动动物（牛羊类） |
| `TamableAnimal` | 可驯服动物（狼猫类） |
| `FlyingAnimal`（接口） | 标记飞行能力，实际继承 `Animal` 或 `Mob` |

**必须写的方法：**
- `registerGoals()` — 编排 AI
- 静态 `createAttributes()` — 生命/速度/攻击

**常用可选方法：**
- `causeFallDamage()` — 返回 false 免疫摔落
- `getHurtSound()` / `getDeathSound()` / `getAmbientSound()` — 音效
>注：这些基类与接口非常多，本节只介绍最常用的。
---

## 第 3 步：注册 EntityType

在 `ModEntities.java` 中添加一行：

```java
public static final DeferredHolder<EntityType<?>, EntityType<XxxEntity>> XXX =
        ENTITY_TYPES.register("注册名",
                () -> EntityType.Builder.of(XxxEntity::new, MobCategory.MONSTER)
                        .sized(0.6F, 0.8F)           // 碰撞箱 宽×高
                        .eyeHeight(0.7F)              // 眼睛高度
                        .clientTrackingRange(8)        // 客户端追踪范围(区块)
                        .updateInterval(3)             // 移动生物用3，静态实体用20
                        .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "注册名"))));
```

**关键参数：**
- `MobCategory.MONSTER` — 敌对对；`CREATURE` — 动物；`AMBIENT` — 环境生物
- `updateInterval(3)` — 同步频率：**移动生物用 3**（每秒 ~6.6 次），静态实体用 20（每秒 1 次）。设太大移动会卡顿。

---

## 第 4 步：注册属性

在 `ModEntityAttributes.java` 中添加一行：

```java
event.put(ModEntities.XXX.get(), XxxEntity.createAttributes().build());
```

此文件使用 `@EventBusSubscriber` 自动注册，无需手动调用。

---

## 第 5 步：创建模型

在 `client/model/monster/` 下创建 Model 类，继承 `EntityModel<LivingEntityRenderState>`：

1. 用 **BlockBench** 建模（Modded Entity 项目）
2. 导出：File → Export → Export Java Entity
3. 复制 `createBodyLayer()` 方法体到模型类
4. 实现 `setupAnim(LivingEntityRenderState state)` 做动画
5. 定义 `LAYER_LOCATION` 常量（客户端注册时需要）

---

## 第 6 步：创建渲染器

在 `client/renderer/entity/` 下创建 Renderer 类：

```java
public class XxxRenderer extends MobRenderer<XxxEntity, LivingEntityRenderState, XxxModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/entity/xxx.png");

    public XxxRenderer(EntityRendererProvider.Context context) {
        super(context, new XxxModel(context.bakeLayer(XxxModel.LAYER_LOCATION)), 0.5F);
    }

    @Override  // MC 26.1 必须实现
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
```

纹理图放到 `src/main/resources/assets/example/textures/entity/` 下。

---

## 第 7 步：客户端注册

在 `ExampleModClient.java` 中添加两条：

```java
event.registerEntityRenderer(ModEntities.XXX.get(), XxxRenderer::new);
event.registerLayerDefinition(XxxModel.LAYER_LOCATION, XxxModel::createBodyLayer);
```

---

## 第 8 步（可选）：刷怪蛋

在 `ModItems.java` 中添加：

```java
// MC 26.1: 通过 DataComponents.ENTITY_DATA 组件关联实体类型
public static final DeferredItem<Item> XXX_SPAWN_EGG = ITEMS.registerItem("xxx_spawn_egg",
        p -> new SpawnEggItem(p.useItemDescriptionPrefix()
                .component(DataComponents.ENTITY_DATA,
                        TypedEntityData.of(ModEntities.XXX.get(), new CompoundTag()))));
```

然后在 `ExampleMod.java` 的 `addCreative()` 中加入 `CreativeModeTabs.SPAWN_EGGS` 标签页：
```java
event.accept(ModItems.XXX_SPAWN_EGG.get());
```

---

## 第 9 步（可选）：数据生成

**战利品表** — `ModEntityLootTablesProvider.java` 中添加：
```java
this.add(ModEntities.XXX.get(), LootTable.lootTable()...);
```

**语言文件** — `ModEnUsLangProvider.java` 中添加：
```java
add(ModEntities.XXX.get(), "English Name");
add(ModItems.XXX_SPAWN_EGG.get(), "Xxx Spawn Egg");
```

**刷怪蛋模型** — `ModModelsProvider.java` 中添加：
```java
itemModels.generateFlatItem(ModItems.XXX_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
```

运行 `./gradlew runData` 生成 JSON 文件。

---

## 第 10 步：测试

```bash
./gradlew compileJava        # 编译检查
./gradlew runData             # 生成数据文件
./gradlew runClient           # 启动游戏
```

游戏内测试：
```
/summon example:xxx ~ ~ ~                       # 召唤实体
/summon example:xxx ~ ~1 ~ {NoAI:1b}            # 无 AI 召唤（检查模型）
/kill @e[type=example:xxx]                      # 清除所有
```

---

## 新增实体检查清单

- [ ] 实体类创建在正确的 `entity/<类型>/` 包下
- [ ] 继承合适的基类（Monster / Animal / ...）
- [ ] `registerGoals()` 编排 AI
- [ ] `createAttributes()` 静态方法
- [ ] `ModEntities.java` 注册 EntityType
- [ ] `ModEntityAttributes.java` 注册属性
- [ ] 模型类在 `client/model/<类型>/` 下
- [ ] 渲染器在 `client/renderer/entity/` 下
- [ ] `createRenderState()` 实现
- [ ] `ExampleModClient.java` 注册渲染器 + 模型层
- [ ] 纹理文件在 `assets/<modid>/textures/entity/` 下
- [ ] `updateInterval(3)` — 移动生物不要用 20
- [ ] `./gradlew compileJava` 编译通过
- [ ] `/summon` 命令测试通过

---

## 文件模板速记

| 步骤 | 文件位置 | 关键内容 |
|-----|---------|---------|
| 实体类 | `entity/<类型>/XxxEntity.java` | extends Monster, registerGoals, createAttributes |
| 注册 | `entity/ModEntities.java` | 一行 `DeferredHolder` + `EntityType.Builder` |
| 属性 | `entity/ModEntityAttributes.java` | 一行 `event.put()` |
| 模型 | `client/model/<类型>/XxxModel.java` | extends EntityModel<LivingEntityRenderState> |
| 渲染器 | `client/renderer/entity/XxxRenderer.java` | extends MobRenderer, createRenderState |
| 客户端 | `ExampleModClient.java` | 两条 event.register... |
| 刷怪蛋 | `item/ModItems.java` | SpawnEggItem + ENTITY_DATA 组件 |
| 战利品 | `datagen/ModEntityLootTablesProvider.java` | this.add(...) |
| 语言 | `datagen/ModEnUsLangProvider.java` | add(Entity, "名称") |
| 纹理 | `assets/<modid>/textures/entity/` | 64×32 或 64×64 PNG |
