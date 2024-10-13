package net.fullstackjones.bigbraincurrency.block.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ShopBlockEntityRenderer implements BlockEntityRenderer<ShopBlockEntity> {
    public ShopBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ShopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.05, 1.05, 0.05);
        poseStack.scale(0.9f, 0.9f, 0.9f);

        // Render glass block
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.GLASS.defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);

        // Render item inside glass block
        ItemStack itemStack = blockEntity.getItems().get(31); // Assuming the first item is for sale
        if (!itemStack.isEmpty()) {
            poseStack.translate(0.5, 0.2, 0.5);
            poseStack.scale(1f, 1f, 1f);
            long time = System.currentTimeMillis();
            float angle = (time % 3600) / 2.0f;
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));

            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, null, 0);
        }

        poseStack.popPose();
    }
}
