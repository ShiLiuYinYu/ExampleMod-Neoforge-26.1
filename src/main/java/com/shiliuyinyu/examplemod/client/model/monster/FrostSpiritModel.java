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
 * 冰霜精灵实体模型。
 * <p>
 * 人形结构（头 + 身体 + 双臂），无腿部（浮空生物）。
 * 纹理 64×32，对应各部件 UV 偏移：
 * <pre>
 *   头部: (0, 0)   8×8
 *   身体: (0, 16)  8×12
 *   手臂: (32, 0)  4×12
 * </pre>
 * <p>
 * 可通过 BlockBench 导出 {@link #createBodyLayer()} 的方法体来替换模型。
 */
public class FrostSpiritModel extends EntityModel<LivingEntityRenderState> {

    /** 模型层注册键，客户端注册时必须与此值一致 */
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "frost_spirit"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public FrostSpiritModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
    }

    /**
     * 构建模型的层定义（LayerDefinition）。
     * 此方法的方法体通常由 BlockBench 的 "Export Java Entity" 功能生成。
     *
     * @return 包含网格定义和纹理尺寸的 LayerDefinition
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 头部: 8×8×8，纹理区域 (0,0)
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 身体: 8×12×4，纹理区域 (0,16)
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 左臂: 4×12×4，纹理区域 (32,0)
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        // 右臂: 镜像左臂，纹理区域 (32,0)
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(32, 0)
                        .mirror()
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    /**
     * 每帧动画更新。
     * 从 {@link LivingEntityRenderState} 中读取当前实体的旋转和步行参数，
     * 计算头部朝向和手臂摆动。
     */
    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        // 头部跟随实体朝向
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);

        // 手臂随步行节奏摆动（相位差 π 实现左右交替）
        this.rightArm.xRot = (float) Math.cos(state.walkAnimationPos * 0.6662F + Math.PI) * 1.4F * state.walkAnimationSpeed;
        this.leftArm.xRot = (float) Math.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
    }
}
