package net.fullstackjones.bigbraincurrency.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SimpleShopBlockEntityRenderer implements BlockEntityRenderer<SimpleShopBlockEntity> {
    public SimpleShopBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SimpleShopBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RenderWindow(poseStack, bufferSource, packedLight, packedOverlay);

        int saleItemQuantity = blockEntity.data.getSaleQuantity();
        ItemStack itemStack = blockEntity.getItemStack();
        itemStack.setCount(saleItemQuantity);

        RenderSaleItem(poseStack, bufferSource, packedLight, packedOverlay, itemStack);
        float playerAngle = getAngle(blockEntity);
        if (!itemStack.isEmpty()) {
            RenderQuantityLabel(poseStack, bufferSource, packedLight, playerAngle, itemStack);
            RenderItemNameLabel(poseStack, bufferSource, packedLight, playerAngle, itemStack);
        }
        RenderPriceLabel(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, playerAngle);

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

    private static void RenderItemNameLabel (PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float playerAngle, ItemStack itemStack){
        poseStack.pushPose();
        poseStack.translate(0.5, 1.3, 0.5); // Adjust position as needed
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(playerAngle));
        poseStack.scale(0.01f, 0.01f, 0.01f);
        String itemName = itemStack.getItem().getDescription().getString();
        Font font = Minecraft.getInstance().font;
        float textWidth = font.width(itemName) / 2.0f;
        float textHeight = font.lineHeight / 2.0f;
        poseStack.translate(-textWidth, -textHeight, 0);
        Matrix4f matrix = poseStack.last().pose();
        Minecraft.getInstance().font.drawInBatch(itemName, 0.0f, 0.0f, 0xFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
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

    private static void RenderPriceLabel(SimpleShopBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float playerAngle) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.2, 0.5); // Adjust position as needed
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(playerAngle));
        poseStack.scale(0.01f, 0.01f, 0.01f); // Smaller text size
        ItemStack[] coins = CurrencyUtil.convertValueToCoins(blockEntity.data.getPrice());
        int priceAmounts = 0;
        for (ItemStack coin : coins) {
            if (!coin.isEmpty()) {
                priceAmounts++;
            }
        }

        float xOffset = 0;

        if(priceAmounts == 4){
            xOffset -= 40;
        } else if (priceAmounts == 3){
            xOffset -= 25;
        } else if(priceAmounts == 2){
            xOffset -= 15;
        }

        for (ItemStack coin : coins) {
            if (!coin.isEmpty()) {
                renderCoinWithCount(poseStack, bufferSource, packedLight, packedOverlay, coin, coin.getCount(), xOffset);
                xOffset += 22;
            }
        }

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5, 1.15, 0.5); // Adjust position as needed
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(playerAngle));
        poseStack.scale(0.01f, 0.01f, 0.01f); // Smaller text size

        xOffset = -5;

        if(priceAmounts == 4){
            xOffset -= 40;
        } else if (priceAmounts == 3){
            xOffset -= 25;
        } else if(priceAmounts == 2){
            xOffset -= 15;
        }
        for (ItemStack coin : coins) {
            if (!coin.isEmpty()) {
                renderCoinName(poseStack, bufferSource, packedLight, coin.getItem().getDescription().getString(), xOffset);
                xOffset += 22;
            }
        }

        poseStack.popPose();
    }

    private static void renderCoinWithCount(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, ItemStack coinStack, int count, float xOffset) {
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
        font.width(countText);
    }

    private static void renderCoinName(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, String coinName, float xOffset) {
        poseStack.pushPose();
        poseStack.translate(xOffset, 0, 0);
        poseStack.scale(0.3f, 0.3f, 0.3f);
        poseStack.translate(0, -0.2, 0); // Adjust position for the count text
        Font font = Minecraft.getInstance().font;
        Matrix4f matrix = poseStack.last().pose();
        font.drawInBatch(coinName, 0, 0, 0xFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();

        // Calculate and return the width of the coin and label
        font.width(coinName);
    }

    private static void RenderSaleItem(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, ItemStack item) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.45, 0.5);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        long time = System.currentTimeMillis();
        float angle = (time % 7200) / 20.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, null, 0);
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
