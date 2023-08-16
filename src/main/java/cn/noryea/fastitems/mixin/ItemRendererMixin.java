package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.SimpleItemModel;
import cn.noryea.fastitems.config.FastItemsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private final SimpleItemModel flattenedModel = new SimpleItemModel();
    @Unique
    private ModelTransformationMode renderMode;

    @Inject(method = "renderItem*", at = @At("HEAD"))
    private void getRenderType(ItemStack itemStack, ModelTransformationMode transformationMode, boolean leftHand, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        this.renderMode = transformationMode;
    }

    @ModifyVariable(method = "renderBakedItemModel", at = @At("HEAD"), index = 1, argsOnly = true)
    private BakedModel useSimpleItemModel(BakedModel model, BakedModel arg, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
        if(FastItemsConfig.enable && !FastItemsConfig.renderSidesOfItems && !stack.isEmpty() && !model.hasDepth() && renderMode == ModelTransformationMode.GROUND) {
            flattenedModel.setItem(model);
            return flattenedModel;
        } else
            return model;
    }

}
