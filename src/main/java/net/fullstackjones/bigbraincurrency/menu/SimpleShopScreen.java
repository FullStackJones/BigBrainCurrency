package net.fullstackjones.bigbraincurrency.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SimpleShopScreen extends AbstractContainerScreen<SimpleShopMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BigBrainCurrency.MODID,"textures/gui/simpleshop.png");

    private static final WidgetSprites COPPERCOIN_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","coppercoin"),
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","coppercoin_focused")
    );
    private static final WidgetSprites SILVERCOIN_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","silvercoin"),
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","silvercoin_focused")
    );
    private static final WidgetSprites GOLDCOIN_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","goldcoin"),
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","goldcoin_focused")
    );
    private static final WidgetSprites PINKCOIN_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","pinkcoin"),
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","pinkcoin_focused")
    );
    private static final WidgetSprites PLUS_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","plus"),
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","plus_focused")
    );
    private static final WidgetSprites MINUS_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","minus"),
            ResourceLocation.fromNamespaceAndPath("bigbraincurrency","minus_focused")
    );

    private static final Component STOCK_TEXT = Component.translatable("container.bigbraincurrency.shopentity.stock");
    private static final Component SALEQUANTITY_TEXT = Component.translatable("container.bigbraincurrency.shopentity.salequantity");
    private static final Component PRICE_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Price");
    private static final Component PROFIT_TEXT = Component.translatable("container.bigbraincurrency.shopentity.Profit");

    public SimpleShopScreen(SimpleShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 256;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
        // 99% sure this is not the right way to do this
        ImageButton copperPrice = new ImageButton(this.leftPos + 65,this.topPos + 100,20,20, COPPERCOIN_SPRITE, Button ->{})
        {
            @Override
            protected boolean isValidClickButton(int pButton) {
                if(pButton == 0 || pButton == 1){
                    return true;
                }
                return false;
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);
                if(button == 1){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 1);
                }
                if(button == 0){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 0);
                }
            }
        };
        ImageButton silverPrice = new ImageButton(this.leftPos + 85,this.topPos + 100,20,20, SILVERCOIN_SPRITE, Button ->{})
        {
            @Override
            protected boolean isValidClickButton(int pButton) {
                if(pButton == 0 || pButton == 1){
                    return true;
                }
                return false;
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);
                if(button == 1){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 3);
                }
                if(button == 0){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 2);
                }
            }
        };
        ImageButton goldPrice = new ImageButton(this.leftPos + 105,this.topPos + 100,20,20, GOLDCOIN_SPRITE, Button ->{})
        {
            @Override
            protected boolean isValidClickButton(int pButton) {
                if(pButton == 0 || pButton == 1){
                    return true;
                }
                return false;
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);
                if(button == 1){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 5);
                }
                if(button == 0){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 4);
                }
            }
        };
        ImageButton pinkPrice = new ImageButton(this.leftPos + 125,this.topPos + 100,20,20, PINKCOIN_SPRITE, Button ->{})
        {
            @Override
            protected boolean isValidClickButton(int pButton) {
                if(pButton == 0 || pButton == 1){
                    return true;
                }
                return false;
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);
                if(button == 1){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 7);
                }
                if(button == 0){
                    SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 6);
                }
            }
        };
        this.addRenderableWidget(copperPrice);
        this.addRenderableWidget(silverPrice);
        this.addRenderableWidget(goldPrice);
        this.addRenderableWidget(pinkPrice);

        ImageButton plus = new ImageButton(this.leftPos + 80,this.topPos + 62,15,15, MINUS_SPRITE,(button) ->{}){
            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);
                SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 8);

            }
        };
        ImageButton minus = new ImageButton(this.leftPos + 125,this.topPos + 62,15,15, PLUS_SPRITE,(button) ->{}){
            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);
                SimpleShopScreen.this.minecraft.gameMode.handleInventoryButtonClick(SimpleShopScreen.this.menu.containerId, 9);

            }
        };
        this.addRenderableWidget(plus);
        this.addRenderableWidget(minus);
    }

    private void sendButtonPress(int buttonId) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private void addValueToPrice(int value){
        this.menu.blockEntity.setPrice(this.menu.blockEntity.data.getPrice() + value);
    }

    private void minusValueToPrice(int value){
        this.menu.blockEntity.setPrice(this.menu.blockEntity.data.getPrice() - value);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        this.inventoryLabelY = 144;
        this.titleLabelY = 32;
        this.titleLabelX = 32;



        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY,  0xFFFFFF, false);
        guiGraphics.drawString(this.font, STOCK_TEXT, 35, 46, 0xFFFFFFFF, false);
        guiGraphics.drawString(this.font, SALEQUANTITY_TEXT, 80, 46, 0xFFFFFFFF, false);

        guiGraphics.drawString(this.font, PRICE_TEXT, 95, 90, 0xFFFFFFFF, false);
        guiGraphics.drawString(this.font, PROFIT_TEXT, 35, 90, 0xFFFFFFFF, false);

        String saleQuantity = "x " + String.valueOf(this.menu.blockEntity.data.getSaleQuantity());
        guiGraphics.drawString(this.font, saleQuantity, 100, 65, 0xFFFFFFFF, false);

        ItemStack[] coins = CurrencyUtil.convertValueToCoins(this.menu.blockEntity.data.getPrice());
        guiGraphics.drawString(this.font, String.valueOf(coins[0].getCount()), 72, 120, 0xFFFFFFFF, false);
        guiGraphics.drawString(this.font, String.valueOf(coins[1].getCount()), 92, 120, 0xFFFFFFFF, false);
        guiGraphics.drawString(this.font, String.valueOf(coins[2].getCount()), 112 , 120, 0xFFFFFFFF, false);
        guiGraphics.drawString(this.font, String.valueOf(coins[3].getCount()), 132 , 120, 0xFFFFFFFF, false);
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);


    }
}
