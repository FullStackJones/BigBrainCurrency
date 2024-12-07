package net.fullstackjones.bigbraincurrency.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class SimpleShopScreen extends AbstractContainerScreen<SimpleShopMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BigBrainCurrency.MODID,"textures/gui/simpleshop.png");
    private EditBox pricing;
    private EditBox stockQuantity;

    public SimpleShopScreen(SimpleShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

        this.pricing = new EditBox(this.font, i + 62, j + 24, 103, 12, Component.translatable("container.repair"));
        this.pricing.setCanLoseFocus(false);
        this.pricing.setTextColor(-1);
        this.pricing.setTextColorUneditable(-1);
        this.pricing.setBordered(false);
        this.pricing.setMaxLength(50);
        this.pricing.setResponder(this::onNameChanged);
        this.pricing.setValue("");
        this.addWidget(this.pricing);
        this.pricing.setEditable(this.menu.getSlot(0).hasItem());


        this.stockQuantity = new EditBox(this.font, i + 62, j + 44, 103, 12, Component.translatable("container.repair"));
        this.stockQuantity.setCanLoseFocus(false);
        this.stockQuantity.setTextColor(-1);
        this.stockQuantity.setTextColorUneditable(-1);
        this.stockQuantity.setBordered(false);
        this.stockQuantity.setMaxLength(50);
        this.stockQuantity.setResponder(this::onNameChanged);
        this.stockQuantity.setValue("");
        this.addWidget(this.stockQuantity);
        this.stockQuantity.setEditable(this.menu.getSlot(0).hasItem());
    }

    private void onNameChanged(String p_97899_) {
        Slot slot = this.menu.getSlot(0);
        if (slot.hasItem()) {
            String s = p_97899_;
            if (!slot.getItem().has(DataComponents.CUSTOM_NAME) && p_97899_.equals(slot.getItem().getHoverName().getString())) {
                s = "";
            }


        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

    }
}
