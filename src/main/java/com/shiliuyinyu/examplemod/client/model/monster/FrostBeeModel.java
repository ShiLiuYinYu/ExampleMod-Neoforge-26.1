package com.shiliuyinyu.examplemod.client.model.monster;

import com.shiliuyinyu.examplemod.ExampleMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * 冰霜蜜蜂实体模型。
 * <p>
 * 蜜蜂结构（身体 + 毒刺 + 双翼 + 三对足），纹理 64×64。
 * Blockbench 导出模型，方法体由 {@link #createBodyLayer()} 生成。
 */
public class FrostBeeModel extends EntityModel<LivingEntityRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_bee"), "main");

    private final ModelPart body;
    private final ModelPart stinger;
    private final ModelPart rightwing_bone;
    private final ModelPart leftwing_bone;
    private final ModelPart leg_front;
    private final ModelPart leg_mid;
    private final ModelPart leg_back;

    public FrostBeeModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.stinger = this.body.getChild("stinger");
        this.rightwing_bone = this.body.getChild("rightwing_bone");
        this.leftwing_bone = this.body.getChild("leftwing_bone");
        this.leg_front = this.body.getChild("leg_front");
        this.leg_mid = this.body.getChild("leg_mid");
        this.leg_back = this.body.getChild("leg_back");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(2, 3).addBox(-2.5F, -4.0F, -8.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(2, 0).addBox(1.5F, -4.0F, -8.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.5F, 19.0F, 0.0F));

        PartDefinition stinger = body.addOrReplaceChild("stinger", CubeListBuilder.create()
                .texOffs(26, 7).addBox(0.0F, 0.0F, 4.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -1.0F, 1.0F));

        PartDefinition rightwing_bone = body.addOrReplaceChild("rightwing_bone", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.2618F, -0.2618F, 0.0F));

        PartDefinition leftwing_bone = body.addOrReplaceChild("leftwing_bone", CubeListBuilder.create()
                .texOffs(9, 24).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.2618F, 0.2618F, 0.0F));

        PartDefinition leg_front = body.addOrReplaceChild("leg_front", CubeListBuilder.create()
                .texOffs(26, 1).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, 3.0F, -2.0F));

        PartDefinition leg_mid = body.addOrReplaceChild("leg_mid", CubeListBuilder.create()
                .texOffs(26, 3).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, 3.0F, 0.0F));

        PartDefinition leg_back = body.addOrReplaceChild("leg_back", CubeListBuilder.create()
                .texOffs(26, 5).addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, 3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    /**
     * 每帧动画更新：驱动翅膀扇动和腿部摆动。
     */
    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        // 翅膀扇动：高频正弦摆动模拟飞行时的振翅
        float wingFlap = (float) Math.cos(state.walkAnimationPos * 1.5F) * 0.6F;
        this.rightwing_bone.zRot = wingFlap;
        this.leftwing_bone.zRot = -wingFlap;

        // 腿部随步行节奏摆动
        this.leg_front.xRot = (float) Math.cos(state.walkAnimationPos * 0.6662F) * 0.5F * state.walkAnimationSpeed;
        this.leg_mid.xRot = (float) Math.cos(state.walkAnimationPos * 0.6662F + Math.PI * 0.5F) * 0.5F * state.walkAnimationSpeed;
        this.leg_back.xRot = (float) Math.cos(state.walkAnimationPos * 0.6662F + Math.PI) * 0.5F * state.walkAnimationSpeed;
    }
}
