package net.fullstackjones.bigbraincurrency.block.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.StringJoiner;

public class ShopBlockEntityRenderer implements BlockEntityRenderer<ShopBlockEntity> {
    public ShopBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ShopBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RenderWindow(poseStack, bufferSource, packedLight, packedOverlay);

        ItemStack saleItem = blockEntity.shopItems.getStackInSlot(31);
        if(!saleItem.isEmpty()){
            RenderSaleItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
            ItemStack itemStack = blockEntity.shopItems.getStackInSlot(31);
            float playerAngle = getAngle(blockEntity);
            if (!itemStack.isEmpty()) {
                RenderQuantityLabel(poseStack, bufferSource, packedLight, playerAngle, itemStack);
            }
            RenderPriceLabel(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, playerAngle);
        }
    }

    @Override
    public int getViewDistance() {
        return 16;
    }

    private static void RenderQuantityLabel(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float playerAngle, ItemStack itemStack) {
        poseStack.pushPose();
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
        poseStack.popPose();
    }

    private static float getAngle(BlockEntity blockEntity) {
        Player closestPlayer = blockEntity.getLevel().getNearestPlayer(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), 10, false);
        if(closestPlayer == null){
            return 0;
        }
        double dx = closestPlayer.getX() - (blockEntity.getBlockPos().getX() + 0.5);
        double dz = closestPlayer.getZ() - (blockEntity.getBlockPos().getZ() + 0.5);
        return (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90;

    }

    private static void RenderPriceLabel(ShopBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float playerAngle) {
        ItemStack pinkCoinStack = new ItemStack(ModItems.PINKCOIN.get());
        ItemStack goldCoinStack = new ItemStack(ModItems.GOLDCOIN.get());
        ItemStack silverCoinStack = new ItemStack(ModItems.SILVERCOIN.get());
        ItemStack copperCoinStack = new ItemStack(ModItems.COPPERCOIN.get());

        poseStack.pushPose();
        poseStack.translate(0.5, 1.2, 0.5); // Adjust position as needed
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(playerAngle));
        poseStack.scale(0.01f, 0.01f, 0.01f); // Smaller text size
        // todo: improve the offset calculations
        float xOffset = 0;
        int priceTypes = 0;
        if(!blockEntity.shopItems.getStackInSlot(32).isEmpty()){
            priceTypes += 1;
        }
        if(!blockEntity.shopItems.getStackInSlot(33).isEmpty()){
            priceTypes += 1;
        }
        if(!blockEntity.shopItems.getStackInSlot(34).isEmpty()){
            priceTypes += 1;
        }
        if(!blockEntity.shopItems.getStackInSlot(35).isEmpty()){
            priceTypes += 1;
        }

        if(priceTypes == 4){
            xOffset -= 40;
        }

        if(priceTypes == 3){
            xOffset -= 25;
        }

        if(priceTypes == 2){
            xOffset -= 10;
        }

        if (!blockEntity.shopItems.getStackInSlot(32).isEmpty()) {
            renderCoinWithCount(poseStack, bufferSource, packedLight, packedOverlay, pinkCoinStack, blockEntity.shopItems.getStackInSlot(32).getCount(), xOffset);
            xOffset += 22; // Adjust spacing as needed
        }
        if (!blockEntity.shopItems.getStackInSlot(33).isEmpty()) {
            renderCoinWithCount(poseStack, bufferSource, packedLight, packedOverlay, goldCoinStack, blockEntity.shopItems.getStackInSlot(33).getCount(), xOffset);
            xOffset += 22; // Adjust spacing as needed
        }
        if (!blockEntity.shopItems.getStackInSlot(34).isEmpty()) {
            renderCoinWithCount(poseStack, bufferSource, packedLight, packedOverlay, silverCoinStack, blockEntity.shopItems.getStackInSlot(34).getCount(), xOffset);
            xOffset += 22; // Adjust spacing as needed
        }
        if (!blockEntity.shopItems.getStackInSlot(35).isEmpty()) {
            renderCoinWithCount(poseStack, bufferSource, packedLight, packedOverlay, copperCoinStack, blockEntity.shopItems.getStackInSlot(35).getCount(), xOffset);
        }

        poseStack.popPose();
    }

    private static float renderCoinWithCount(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, ItemStack coinStack, int count, float xOffset) {
        poseStack.pushPose();
        poseStack.translate(xOffset, 0, 0);
        poseStack.scale(15, 15f, 15f);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        Minecraft.getInstance().getItemRenderer().renderStatic(coinStack, ItemDisplayContext.GUI, packedLight, packedOverlay, poseStack, bufferSource, null, 0);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.scale(0.03f, 0.03f, 0.03f);
        poseStack.translate(10, -2, 0); // Adjust position for the count text
        String countText = "x " + count;
        Font font = Minecraft.getInstance().font;
        Matrix4f matrix = poseStack.last().pose();
        font.drawInBatch(countText, 0, 0, 0xFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();

        // Calculate and return the width of the coin and label
        return 15 + font.width(countText) * 0.04f;
    }

    private static void RenderSaleItem(ShopBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        ItemStack itemStack = blockEntity.shopItems.getStackInSlot(31);
        poseStack.translate(0.5, 1.35, 0.5);
        poseStack.scale(1f, 1f, 1f);
        long time = System.currentTimeMillis();
        float angle = (time % 3600) / 20.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, null, 0);
        poseStack.popPose();
    }

    private static void RenderWindow(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.05, 1.05, 0.05);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.GLASS.defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
