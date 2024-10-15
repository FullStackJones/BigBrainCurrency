package net.fullstackjones.bigbraincurrency.block.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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

        ItemStack saleItem = blockEntity.getShopItems().getStackInSlot(31);
        if(!saleItem.isEmpty()){
            RenderSaleItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
            ItemStack itemStack = blockEntity.getShopItems().getStackInSlot(31);
            float playerAngle = getAngle(blockEntity);
            if (!itemStack.isEmpty()) {
                RenderQuantityLabel(poseStack, bufferSource, packedLight, playerAngle, itemStack);
            }
            RenderPriceLabel(blockEntity, poseStack, bufferSource, packedLight, playerAngle);
        }
    }

    @Override
    public int getViewDistance() {
        return 10;
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

    private static void RenderPriceLabel(ShopBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float playerAngle) {

        String copperPrice= "";
        String silverPrice= "";
        String goldPrice= "";
        String pinkPrice= "";
        if(!blockEntity.shopItems.getStackInSlot(32).isEmpty())
            pinkPrice =  "B x" + blockEntity.shopItems.getStackInSlot(32).getCount();
        if(!blockEntity.shopItems.getStackInSlot(33).isEmpty())
            goldPrice = "G x" + blockEntity.shopItems.getStackInSlot(33).getCount();
        if(!blockEntity.shopItems.getStackInSlot(34).isEmpty())
            silverPrice = "S x" + blockEntity.shopItems.getStackInSlot(34).getCount();
        if(!blockEntity.shopItems.getStackInSlot(35).isEmpty())
            copperPrice = "C x" + blockEntity.shopItems.getStackInSlot(35).getCount();

        StringJoiner priceJoiner = new StringJoiner(" ");
        if (!pinkPrice.isEmpty()) priceJoiner.add(pinkPrice);
        if (!goldPrice.isEmpty()) priceJoiner.add(goldPrice);
        if (!silverPrice.isEmpty()) priceJoiner.add(silverPrice);
        if (!copperPrice.isEmpty()) priceJoiner.add(copperPrice);

        String priceText = priceJoiner.toString();
        poseStack.pushPose();
        poseStack.translate(0.5, 1.2, 0.5); // Adjust position as needed
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(playerAngle));
        poseStack.scale(0.01f, 0.01f, 0.01f); // Smaller text size
        Font font = Minecraft.getInstance().font;

        float textWidth = font.width(priceText) / 2.0f;
        float textHeight = font.lineHeight / 2.0f;

        poseStack.translate(-textWidth, -textHeight, 0);

        Matrix4f matrix = poseStack.last().pose();
        Minecraft.getInstance().font.drawInBatch(priceText, 0.0f, 0.0f, 0xFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    private static void RenderSaleItem(ShopBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        ItemStack itemStack = blockEntity.getShopItems().getStackInSlot(31);
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
