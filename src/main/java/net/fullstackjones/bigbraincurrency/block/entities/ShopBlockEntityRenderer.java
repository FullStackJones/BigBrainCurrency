package net.fullstackjones.bigbraincurrency.block.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fullstackjones.bigbraincurrency.block.ModBlocks;
import net.fullstackjones.bigbraincurrency.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;

public class ShopBlockEntityRenderer implements BlockEntityRenderer<ShopBlockEntity> {
    public ShopBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ShopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.05, 1.05, 0.05);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.GLASS.defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);

        // Render item inside glass block
        ItemStack itemStack = blockEntity.getItems().get(31); // Assuming the first item is for sale
        if(itemStack.isEmpty()) {
            poseStack.popPose();
            return;
        }

        poseStack.translate(0.5, 0.3, 0.5);
        poseStack.scale(1f, 1f, 1f);
        long time = System.currentTimeMillis();
        float angle = (time % 3600) / 20.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, null, 0);
        poseStack.popPose();

        poseStack.pushPose();
        Player closestPlayer = blockEntity.getLevel().getNearestPlayer(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), 10, false);
        if (closestPlayer != null && !itemStack.isEmpty()) {
            double dx = closestPlayer.getX() - (blockEntity.getBlockPos().getX() + 0.5);
            double dz = closestPlayer.getZ() - (blockEntity.getBlockPos().getZ() + 0.5);
            float playerAngle = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90;

            poseStack.translate(0.5, 1.8, 0.5); // Adjust position as needed
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(playerAngle));
            poseStack.scale(0.02f, 0.02f, 0.02f);
            String itemCountText = "x " + itemStack.getCount();
            Font font = Minecraft.getInstance().font;

            float textWidth = font.width(itemCountText) / 2.0f;
            float textHeight = font.lineHeight / 2.0f;

            poseStack.translate(-textWidth, -textHeight, 0);

            Matrix4f matrix = poseStack.last().pose();
            Minecraft.getInstance().font.drawInBatch(itemCountText, 0.0f, 0.0f, 0xFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        }
        poseStack.popPose();


    }
}
