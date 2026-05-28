# Example Mod —— NeoForge 26.1 模组示例

这是一个 NeoForge 模组开发示例项目，展示了模组开发中常见的功能实现方式，适合作为新模组项目的参考或起点。

## 示例内容

- **自定义物品** —— 普通物品、燃料物品、工具（镐斧合一）、护甲、食物
- **自定义方块** —— 矿石、矿物块、门、活板门、作物
- **自定义实体** —— FrostSpirit、FrostBee（含模型、渲染器、战利品表）
- **创造模式物品栏** —— 自定义标签页
- **数据生成** —— 语言文件（中/英）、模型、合成配方、战利品表、标签
- **配置系统** —— NeoForge Config API 使用示例
- **Mixins** —— 基础 Mixin 配置示例

## 使用方式

**不建议直接克隆此项目并修改为自己的模组。** 因为 MOD_ID 不同会导致大量文件需要重命名（包名、资源路径、mixins 配置、语言文件引用等），改起来非常繁琐且容易遗漏。

建议以本项目为**代码参考和框架示例**，按以下步骤搭建自己的模组：

1. 从 [NeoForge 官方 MDK](https://github.com/NeoForged/MDK) 创建新项目，设置好你自己的 `mod_id` 和包名。
2. 参考本项目中各模块的代码结构和实现方式，逐步添加你需要的功能。
3. 运行 `gradlew runClient` 启动 Minecraft 测试客户端。

如果遇到依赖问题，可以执行：

```bash
./gradlew --refresh-dependencies
./gradlew clean
```
>注：可以使用[Neoforge模板网站](https://neoforged.net/mod-generator/)
## 开发环境要求

- JDK 25
- IntelliJ IDEA（推荐）或 Eclipse

## 相关资源

- [NeoForge 文档](https://docs.neoforged.net/)
- [NeoForged Discord](https://discord.neoforged.net/)
- [Mojang 映射名 License](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md)
