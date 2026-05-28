package com.shiliuyinyu.examplemod.client.renderer.entity;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.client.model.monster.FrostSpiritModel;
import com.shiliuyinyu.examplemod.entity.monster.FrostSpiritEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * 冰霜精灵实体渲染器。
 * <p>
 * 泛型参数说明：
 * <ul>
 *   <li>{@code FrostSpiritEntity} — 实体类型</li>
 *   <li>{@code LivingEntityRenderState} — MC 26.1 新增的渲染状态，
 *       模型通过 RenderState 获取动画数据，而非直接读取 Entity</li>
 *   <li>{@code FrostSpiritModel} — 模型类</li>
 * </ul>
 */
public class FrostSpiritRenderer extends MobRenderer<FrostSpiritEntity, LivingEntityRenderState, FrostSpiritModel> {

    /** 纹理文件路径: assets/example/textures/entity/frost_spirit.png */
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/entity/frost_spirit.png");

    public FrostSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new FrostSpiritModel(context.bakeLayer(FrostSpiritModel.LAYER_LOCATION)), 0.5F);
    }

    /**
     * 创建渲染状态实例。MC 26.1 新增要求。
     * 每帧由渲染系统调用，为本次渲染创建新的 state 容器。
     */
    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    /** 返回实体纹理的 {@link Identifier} */
    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
