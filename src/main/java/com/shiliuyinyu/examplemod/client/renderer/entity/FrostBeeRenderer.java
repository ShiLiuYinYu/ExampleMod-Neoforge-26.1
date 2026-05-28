package com.shiliuyinyu.examplemod.client.renderer.entity;

import com.shiliuyinyu.examplemod.ExampleMod;
import com.shiliuyinyu.examplemod.client.model.monster.FrostBeeModel;
import com.shiliuyinyu.examplemod.entity.monster.FrostBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * 冰霜蜜蜂实体渲染器。
 */
public class FrostBeeRenderer extends MobRenderer<FrostBeeEntity, LivingEntityRenderState, FrostBeeModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/entity/frost_bee.png");

    public FrostBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new FrostBeeModel(context.bakeLayer(FrostBeeModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
