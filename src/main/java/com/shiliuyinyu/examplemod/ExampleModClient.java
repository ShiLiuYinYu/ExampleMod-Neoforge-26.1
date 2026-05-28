package com.shiliuyinyu.examplemod;

import com.shiliuyinyu.examplemod.client.model.monster.FrostBeeModel;
import com.shiliuyinyu.examplemod.client.model.monster.FrostSpiritModel;
import com.shiliuyinyu.examplemod.client.renderer.entity.FrostBeeRenderer;
import com.shiliuyinyu.examplemod.client.renderer.entity.FrostSpiritRenderer;
import com.shiliuyinyu.examplemod.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// 此类不会在专用服务端加载，在此处访问客户端代码是安全的。
@Mod(value = ExampleMod.MOD_ID, dist = Dist.CLIENT)
// 使用 EventBusSubscriber 可自动注册类中所有带 @SubscribeEvent 注解的静态方法
@EventBusSubscriber(modid = ExampleMod.MOD_ID, value = Dist.CLIENT)
public class ExampleModClient {
    public ExampleModClient(ModContainer container) {
        // 允许 NeoForge 为此模组的配置项创建配置界面。
        // 配置界面通过 模组列表 > 点击你的模组 > 点击 Config 进入。
        // 别忘了在 en_us.json 中为配置选项添加翻译。
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 一些客户端初始化代码
        ExampleMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        ExampleMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

    }

    // ---- 实体渲染器注册 ----

    /**
     * 注册实体渲染器：将 EntityType 与对应的 EntityRenderer 绑定。
     * 每个自定义实体在此添加一条 registerEntityRenderer 调用。
     */
    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FROST_SPIRIT.get(), FrostSpiritRenderer::new);
        event.registerEntityRenderer(ModEntities.FROST_BEE.get(), FrostBeeRenderer::new);
    }

    /**
     * 注册模型层定义：将 ModelLayerLocation 与 LayerDefinition 工厂绑定。
     * 每个自定义实体模型在此添加一条 registerLayerDefinition 调用。
     */
    @SubscribeEvent
    static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FrostSpiritModel.LAYER_LOCATION, FrostSpiritModel::createBodyLayer);
        event.registerLayerDefinition(FrostBeeModel.LAYER_LOCATION, FrostBeeModel::createBodyLayer);
    }
}
