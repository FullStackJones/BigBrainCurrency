package net.fullstackjones.bigbraincurrency.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

public class BrainBankScreen extends AbstractContainerScreen<BrainBankMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BigBrainCurrency.MODID,"textures/gui/brainbank.png");

    public BrainBankScreen(BrainBankMenu menu, Inventory playerInventory, Component title) {
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
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        this.inventoryLabelY = 98;
        this.titleLabelY = 26;
        this.titleLabelX = 62;

        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY,  0xFFFFFF, false);
        if(this.menu.blockEntity.getData().getHadUbi()){
            this.renderCountdownTimer(guiGraphics);
        }
    }

    private void renderCountdownTimer(GuiGraphics guiGraphics) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midday = now.withHour(12).withMinute(0).withSecond(0).withNano(0);
        if (now.isAfter(midday)) {
            midday = midday.plusDays(1);
        }
        Duration duration = Duration.between(now, midday);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.toSeconds() % 60;

        String timerText = String.format("%02d : %02d : %02d", hours, minutes, seconds);
        guiGraphics.drawString(this.font, timerText, this.titleLabelX -1, this.titleLabelY + 45, 0xFFFFFFFF, false);
    }
}
