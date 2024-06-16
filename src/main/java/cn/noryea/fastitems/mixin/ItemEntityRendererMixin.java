package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.config.FastItemsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    @Final
    @Shadow private ItemRenderer itemRenderer;

    @Final
    @Shadow private Random random;

    protected ItemEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow
    protected abstract int getRenderedAmount(ItemStack stack);

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        //early exit
        if (!FastItemsConfig.enable) {
            return;
        }

        matrixStack.push();
        ItemStack itemStack = itemEntity.getStack();
        long j = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed(j);

        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.getWorld(), null, itemEntity.getId());
        boolean hasDepth = bakedModel.hasDepth();
        this.shadowRadius = FastItemsConfig.castShadows ? 0.15F : 0.0F;

        // up and down
        float l = MathHelper.sin(((float) itemEntity.getItemAge() + g) / 10.0F + itemEntity.uniqueOffset) * 0.1F + 0.1F;
        float m = bakedModel.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        matrixStack.translate(0.0F, l + 0.25F * m, 0.0F);

        // drawing
        matrixStack.multiply(this.dispatcher.getRotation());
        // rotation correcting
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        float o = bakedModel.getTransformation().ground.scale.x();
        float p = bakedModel.getTransformation().ground.scale.y();
        float q = bakedModel.getTransformation().ground.scale.z();
        float s;
        float t;

        int renderedAmount = this.getRenderedAmount(itemStack);

        if (!hasDepth) {
            float r = -0.0F * (float)(renderedAmount - 1) * 0.5F * o;
            s = -0.0F * (float)(renderedAmount - 1) * 0.5F * p;
            t = -0.09375F * (float)(renderedAmount - 1) * 0.5F * q;
            matrixStack.translate(r, s, t);
        }

        for(int u = 0; u < renderedAmount; ++u) {
            matrixStack.push();

            if (u > 0) {
                if (hasDepth) {
                    s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrixStack.translate(s, t, v);
                } else {
                    s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    matrixStack.translate(s, t, 0.0F);
                }
            }
            this.itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
            matrixStack.pop();

            if (!hasDepth) {
                matrixStack.translate(0.0F * o, 0.0F * p, 0.0425F * q);
            }
        }
        matrixStack.pop();
        super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);

        ci.cancel();
    }

}
