package net.fullstackjones.bigbraincurrency.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.fullstackjones.bigbraincurrency.menu.customslots.PricingSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BigBrainCurrency.MODID,"textures/gui/shop.png");
    private final ShopBlockEntity blockEntity;

    private static final Component STOCK_TEXT = Component.translatable("container.bigbraincurrency.shopentity.stock");
    private static final Component ITEM_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Item");
    private static final Component PRICE_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Price");
    private static final Component PROFIT_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Profit");

    public ShopScreen(ShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.blockEntity = menu.getBlockEntity();
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 || button == 1) { // Left click
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;

            for (int i = 32; i <= 35; i++) {
                Slot slot = this.menu.getSlot(i);
                if (isMouseOverSlot(slot, mouseX, mouseY, x, y)) {
                    handlePriceingSlotClick(slot, button);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY, int x, int y) {
        return mouseX >= x + slot.x && mouseX < x + slot.x + 16 && mouseY >= y + slot.y && mouseY < y + slot.y + 16;
    }

    private void handlePriceingSlotClick(Slot slot, int button) {
        ItemStack stack = slot.getItem();
        if (button == 0) { // Left click
            if (stack.isEmpty()) {
                PricingSlot pricingSlot = (PricingSlot) slot;
                Item item = pricingSlot.getCurrencyType().getItem();
                slot.set(item.getDefaultInstance());
            } else {
                stack.grow(1);
            }
        } else if (button == 1) { // Right click
            if (!stack.isEmpty()) {
                stack.shrink(1);
                if (stack.getCount() <= 0) {
                    slot.set(new ItemStack(((PricingSlot) slot).getCurrencyType().getItem(), 0));
                }
            }
        }
        this.menu.slotsChanged(slot.container);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        // Ensure the GUI is synchronized with the server state
        if (blockEntity != null) {
            blockEntity.requestModelDataUpdate();
        }
    }
}
