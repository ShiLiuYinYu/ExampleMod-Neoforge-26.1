# Mod 生物生成系统

> NeoForge 26.1 · 模组生物在 Minecraft 世界中的两种生成机制及完整注册流程

---

## 概览

Minecraft 的生物生成由两条路径组成：

```
┌─────────────────────────────────────────────────────────────┐
│                      ServerLevel 每 tick                     │
│                            │                                │
│          ┌─────────────────┴─────────────────┐              │
│          │                                   │              │
│     NaturalSpawner                    TickCustomSpawners    │
│   （群系自然生成，按 mob cap）           （独立逻辑，不计上限） │
│          │                                   │              │
│    读取群系 MobSpawnSettings            遍历 CustomSpawner   │
│    按权重随机 EntityType                每个实现自己的 tick() │
│          │                                   │              │
│    SpawnPlacements.checkSpawnRules()    SpawnPlacements.check │
│          │                                   │              │
│    create + finalizeSpawn               create + finalize    │
└─────────────────────────────────────────────────────────────┘
```

本模组两种生物各采用一条路径：

| 生物 | 生成方式 | 对应原版类比 |
|------|---------|------------|
| **FrostSpirit** | NaturalSpawner + Biome Modifier（群系自然生成） | 僵尸、骷髅、苦力怕 |
| **FrostBee** | CustomSpawner 独立生成器 | 幻翼（Phantom）、灾厄巡逻队（Patrol） |

---

## 方式一：FrostSpirit —— 群系自然生成（Biome Modifier）

### 原理

`NaturalSpawner` 每个 tick 遍历玩家周围的 chunk，读取当前群系的 `MobSpawnSettings`（生成列表），按**权重（weight）**随机选出 `EntityType`，然后调用 `SpawnPlacements.checkSpawnRules()` 验证生成位置。

`MobSpawnSettings` 由**群系 JSON 文件**定义，NeoForge 提供了 **Biome Modifier** 机制让模组向已有群系追加生成条目，而无需覆写群系文件。

### 相关文件

```
entity/
├── monster/
│   └── FrostSpiritEntity.java          ← checkSpawnRules() 位置谓词
└── spawner/
    └── ModSpawners.java                ← SpawnPlacements 注册

datagen/
└── ModBiomeModifiersProvider.java              ← RegistrySetBuilder 生成 biome_modifier JSON

ExampleModDataGenerator.java            ← 注册 ModBiomeModifiersProvider.BUILDER
```

### 第 1 步：生成位置谓词

在实体类中定义静态方法，检查群系温度和光照条件：

```java
// FrostSpiritEntity.java
public static boolean checkSpawnRules(
        EntityType<FrostSpiritEntity> type,
        ServerLevelAccessor level,
        EntitySpawnReason reason,
        BlockPos pos,
        RandomSource random) {

    return level.getBiome(pos).value().getBaseTemperature() < 0.25F   // 寒冷群系
            && Monster.checkMonsterSpawnRules(type, level, reason, pos, random); // 原版光照检查
}
```

- `getBaseTemperature() < 0.25F` — 限定寒冷群系
- `Monster.checkMonsterSpawnRules()` — 原版怪物光照规则（仅黑暗处生成）

### 第 2 步：注册 SpawnPlacements

在 Mod 总线事件中注册位置类型 + 谓词：

```java
// ModSpawners.java → registerSpawnPlacements()
event.register(
        ModEntities.FROST_SPIRIT.get(),
        SpawnPlacementTypes.ON_GROUND,                       // 地面生成
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,           // 使用顶部实体方块高度图
        FrostSpiritEntity::checkSpawnRules,                  // 位置谓词
        RegisterSpawnPlacementsEvent.Operation.REPLACE);
```

### 第 3 步：生成 Biome Modifier JSON（数据生成）

`ModBiomeModifiers` 类内部构造一个 `RegistrySetBuilder`，定义群系修饰器：

```java
// ModBiomeModifiersProvider.java
public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
    .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
        HolderGetter<Biome> biomes = bootstrap.lookup(Registries.BIOME);
        HolderSet<Biome> coldBiomes = biomes.getOrThrow(COLD_OVERWORLD);

        bootstrap.register(key,
            BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                coldBiomes,
                new Weighted<>(
                    new MobSpawnSettings.SpawnerData(
                        (EntityType<?>) ModEntities.FROST_SPIRIT.get(), 1, 3),
                    80)));  // weight=80, minCount=1, maxCount=3
    });
```

- `COLD_OVERWORLD` 标签：`#c:is_cold/overworld`，涵盖积雪平原、冰刺之地、积雪针叶林、冻洋等全部主世界寒冷群系
- 权重 80（僵尸 100、骷髅 80 参考基准）
- 每组 1~3 只

运行 `runData` 后自动生成 `data/example/neoforge/biome_modifier/add_frost_spirit.json`：

```json
{
  "type": "neoforge:add_spawns",
  "biomes": "#c:is_cold/overworld",
  "spawners": {
    "type": "example:frost_spirit",
    "weight": 80,
    "minCount": 1,
    "maxCount": 3
  }
}
```

### 第 4 步：注册到 datagen

```java
// ExampleModDataGenerator.java → gatherData()
event.createProvider((output, registries) ->
    new DatapackBuiltinEntriesProvider(output, registries,
        ModBiomeModifiersProvider.BUILDER, Set.of(ExampleMod.MOD_ID)));
```

### FrostSpirit 生成流程总结

```
玩家进入寒冷群系
  → NaturalSpawner 读取 biome_modifier/add_frost_spirit.json
    → 按权重 80 随机选中 FrostSpirit
      → SpawnPlacements.checkSpawnRules()
        → FrostSpiritEntity.checkSpawnRules()
          → 温度 < 0.25 ✓ → 光照 < 7 ✓
            → create() + finalizeSpawn() → 生成成功
```

---

## 方式二：FrostBee —— 独立生成器（CustomSpawner）

### 原理

`CustomSpawner` 是一个简单接口（`void tick(ServerLevel, boolean)`），每个 `ServerLevel` 维护一个 `List<CustomSpawner>`，在 `tickCustomSpawners()` 中逐一遍历。原版用它处理**不适用群系生成列表**的特殊生成逻辑（幻翼、巡邏隊、貓）。

实现 `CustomSpawner` 的类拥有**完全的生成控制权**：决定何时、何地、以何种条件生成生物，不受群系生成权重和 mob cap 约束。

### 相关文件

```
entity/
├── monster/
│   └── FrostBeeEntity.java             ← checkSpawnRules() 位置谓词
└── spawner/
    ├── FrostBeeSpawner.java             ← CustomSpawner 实现
    └── ModSpawners.java                 ← SpawnPlacements 注册 + CustomSpawner 注入

ExampleMod.java                          ← 注册 NeoForge.EVENT_BUS 事件
```

### 第 1 步：生成位置谓词

```java
// FrostBeeEntity.java
public static boolean checkSpawnRules(
        EntityType<FrostBeeEntity> type,
        ServerLevelAccessor level,
        EntitySpawnReason reason,
        BlockPos pos,
        RandomSource random) {

    return level.getBiome(pos).value().getBaseTemperature() < 0.35F   // 寒冷群系
            && Monster.checkAnyLightMonsterSpawnRules(type, level, reason, pos, random);
            // ↑ 无光照限制（飞行怪物在洞穴和地表均可生成）
}
```

### 第 2 步：实现 CustomSpawner

`FrostBeeSpawner` 仿照原版 `CatSpawner` / `PhantomSpawner` 模式：

```java
public class FrostBeeSpawner implements CustomSpawner {
    private int nextTick;  // 倒计时器，到零时触发一次生成

    @Override
    public void tick(ServerLevel level, boolean spawnEnemies) {
        // 1. 快速退出：和平模式 / 游戏规则禁用
        if (!spawnEnemies) return;
        if (!level.getGameRules().get(GameRules.SPAWN_MOBS)) return;

        // 2. 倒计时
        this.nextTick--;
        if (this.nextTick > 0) return;
        this.nextTick = 500 + random.nextInt(400); // 25~45 秒一次

        // 3. 找到目标玩家
        Player player = level.getRandomPlayer();

        // 4. 在玩家 24~48 格范围内尝试寻找合法生成位置（最多 10 次）
        // 5. 检查：群系温度 < 0.35、附近 FrostBee 不超过 3 只、空地合法
        // 6. 每组 1~2 只，create + finalizeSpawn + addFreshEntity
    }
}
```

关键参数：

| 参数 | 值 | 说明 |
|------|----|----|
| 生成间隔 | 25~45 秒 | 比幻翼（60~120秒）稍频繁 |
| 生成距离 | 24~48 格 | 不在玩家脸上生成 |
| 温度上限 | 0.35 | 比 FrostSpirit 稍宽松，覆盖更多寒冷群系 |
| 附近上限 | 3 只 | 32 格范围内不超过 3 只 |
| 每组数量 | 1~2 只 | 比 FrostSpirit 少 |
| Y 轴范围 | +4 ~ -8（相对于玩家） | 飞行生物强调 Y 轴分布 |

### 第 3 步：注入 CustomSpawner 到 ServerLevel

通过 `ModifyCustomSpawnersEvent`（Game 总线）添加到世界：

```java
// ModSpawners.java → onModifyCustomSpawners()
public static void onModifyCustomSpawners(ModifyCustomSpawnersEvent event) {
    event.addCustomSpawner(new FrostBeeSpawner());  // 每个 ServerLevel 新建独立实例
}
```

在 Mod 构造方法中注册：

```java
// ExampleMod.java
NeoForge.EVENT_BUS.addListener(ModSpawners::onModifyCustomSpawners);
```

### FrostBee 生成流程总结

```
ServerLevel.tickCustomSpawners()
  → FrostBeeSpawner.tick()
    → 倒计时归零？
      → 随机玩家位置 ± 24~48 格
        → 群系温度 < 0.35 ✓
          → 附近 FrostBee < 3 ✓
            → NaturalSpawner.isValidEmptySpawnBlock ✓
              → create() + finalizeSpawn() → 生成成功
```

---

## 公共基础设施

### SpawnPlacements 注册

两种生成方式**都依赖** `SpawnPlacements` 注册。它在 Mod 总线上通过 `RegisterSpawnPlacementsEvent` 注册：

```java
// ModSpawners.java
@EventBusSubscriber(modid = ExampleMod.MOD_ID)  // 自动订阅 Mod 总线
public class ModSpawners {

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // FrostSpirit — ON_GROUND（地面生成）
        event.register(ModEntities.FROST_SPIRIT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FrostSpiritEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // FrostBee — NO_RESTRICTIONS（飞行生物，无高度限制）
        event.register(ModEntities.FROST_BEE.get(),
                SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FrostBeeEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
```

### SpawnPlacementTypes 说明

| 类型 | 适用场景 | 对应原版生物 |
|------|---------|------------|
| `ON_GROUND` | 地面生成，检测脚下方块 | 僵尸、骷髅、苦力怕 |
| `IN_WATER` | 水下生成 | 鱿鱼、溺尸 |
| `IN_LAVA` | 熔岩表面 | 炽足兽 |
| `NO_RESTRICTIONS` | 无限制，不检测方块 | 幻翼、卫道士 |

### 实体谓词方法

| 原版方法 | 作用 |
|---------|------|
| `Monster.checkMonsterSpawnRules()` | 非和平模式 + 黑暗处（光照 < 7）+ Mob 基础检查 |
| `Monster.checkAnyLightMonsterSpawnRules()` | 非和平模式 + Mob 基础检查（**不限光照**） |
| `Monster.checkSurfaceMonstersSpawnRules()` | 上述 + 必须露天（canSeeSky） |
| `Mob.checkMobSpawnRules()` | 仅 Mob 基础检查（碰撞箱、方块合法性） |

---

## 文件索引

```
src/main/java/com/shiliuyinyu/examplemod/
├── ExampleMod.java                       ← 构造方法中注册 NeoForge.EVENT_BUS 事件
├── ExampleModDataGenerator.java          ← gatherData 中注册 ModBiomeModifiersProvider.BUILDER
│
├── entity/
│   ├── monster/
│   │   ├── FrostSpiritEntity.java        ← checkSpawnRules() 生成谓词
│   │   └── FrostBeeEntity.java           ← checkSpawnRules() 生成谓词
│   └── spawner/
│       ├── FrostBeeSpawner.java           ← CustomSpawner 实现（独立生成）
│       └── ModSpawners.java              ← SpawnPlacements 注册 + CustomSpawner 注入
│
└── datagen/
    └──ModBiomeModifiersProvider.java            ← RegistrySetBuilder 定义群系 modifier
```

运行时生成（`runData` 任务输出）：

```
data/example/neoforge/biome_modifier/
└── add_frost_spirit.json                 ← 将 FrostSpirit 添加到 #c:is_cold/overworld 群系
```

---

## 如何为新生物选择生成方式

```
是否需要特殊的生成条件（时间、天气、结构关联等）？
├── 否 → 使用 Biome Modifier（方式一）
│        像僵尸/骷髅一样，在特定群系按权重自然生成
│        只需：SpawnPlacements + 群系 modifier JSON
│
└── 是 → 实现 CustomSpawner（方式二）
         像幻翼/巡逻队一样，自己控制所有生成逻辑
         需要：SpawnPlacements + CustomSpawner 类 + ModifyCustomSpawnersEvent 注册
```

| 考虑因素 | Biome Modifier | CustomSpawner |
|---------|---------------|---------------|
| 代码量 | 少（数据驱动） | 多（完整逻辑） |
| 灵活性 | 受 NaturalSpawner 框架约束 | 完全自由 |
| mob cap | 是（与原版共享生物上限） | 否（独立于上限） |
| 可配置性 | JSON 数据包可覆盖 | 需修改代码 |
| 典型用途 | 主世界通用怪物 | 特殊机制生物 |
