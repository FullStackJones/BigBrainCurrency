package net.fullstackjones.bigbraincurrency.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BigBrainCurrency.MODID,"textures/gui/shop.png");

    private static final Component STOCK_TEXT = Component.translatable("container.bigbraincurrency.shopentity.stock");
    private static final Component ITEM_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Item");
    private static final Component PRICE_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Price");
    private static final Component PROFIT_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Profit");
    private float yMouse;
    private float xMouse;

    public ShopScreen(ShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        this.inventoryLabelY = 145;
        this.titleLabelY = 45;
        this.titleLabelX = 8;

        guiGraphics.drawString(this.font, ITEM_TEXT, 5, 5,  0x3F3F3F, false);
        guiGraphics.drawString(this.font, PRICE_TEXT, 98, 5,  0x3F3F3F, false);
        guiGraphics.drawString(this.font, PROFIT_TEXT, 98, 45,  0x3F3F3F, false);
        guiGraphics.drawString(this.font, STOCK_TEXT, 8, 65,  0x3F3F3F, false);
    }
}
