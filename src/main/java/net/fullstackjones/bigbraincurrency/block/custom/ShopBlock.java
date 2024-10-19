package net.fullstackjones.bigbraincurrency.block.custom;

import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;
import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.fullstackjones.bigbraincurrency.data.BankDetails;
import net.fullstackjones.bigbraincurrency.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.fullstackjones.bigbraincurrency.data.ModAttachmentTypes.BANKDETAILS;

public class ShopBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ShopBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShopBlockEntity shopBlockEntity) {
                shopBlockEntity.setOwnerUUID(placer.getUUID());
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof ShopBlockEntity shop) {
                if (shop.getOwnerUUID() == player.getUUID()) {
                    ((ServerPlayer) player).openMenu(new SimpleMenuProvider(shop, Component.literal("shop")), pos);
                } else if (player.isCreative()) {
                    ((ServerPlayer) player).openMenu(new SimpleMenuProvider(shop, Component.literal("shop")), pos);
                }
                else{
                    player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.notOwner"));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    //todo: refactor this method to be more readable
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ShopBlockEntity shop) {
            if (shop.getOwnerUUID().equals(player.getUUID())) {
                if(stack.getItem().equals(ModItems.MONEY_POUCH.asItem())){
                    if(!shop.IsThereProfit()){
                        player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.noProfit"));
                        return ItemInteractionResult.SUCCESS;
                    }

                    BankDetails details = player.getData(BANKDETAILS);
                    int playerBalance = details.getBankBalanceValue();
                    if(playerBalance == CurrencyUtil.getMaxValue()){
                        player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.pouchFull"));
                        return ItemInteractionResult.SUCCESS;
                    }

                    int ShopBalance = shop.GetShopBalance();
                    int calculatedBalance = playerBalance + ShopBalance;
                    if(calculatedBalance > CurrencyUtil.getMaxValue()){
                        playerBalance = CurrencyUtil.getMaxValue();
                    }
                    else{
                        playerBalance = calculatedBalance;
                    }

                    int remainingBalance = calculatedBalance - CurrencyUtil.getMaxValue();
                    if(remainingBalance < 0){
                        remainingBalance = 0;
                    }

                    ItemStack[] remainingCoins = CurrencyUtil.convertValueToCoins(remainingBalance);
                    shop.clearProfit();
                    shop.UpdateShopProfits(remainingCoins);

                    ItemStack[] playerCoins = CurrencyUtil.convertValueToCoins(playerBalance);
                    BankDetails updatedDetails = details.update(playerCoins[0].getCount(), playerCoins[1].getCount(), playerCoins[2].getCount(), playerCoins[3].getCount());
                    player.setData(BANKDETAILS, updatedDetails);

                    return ItemInteractionResult.SUCCESS;

                } else if (stack.getItem() == shop.shopItems.getStackInSlot(31).getItem() && !shop.shopItems.getStackInSlot(31).isEmpty()) {
                    InsertStackIntoStock(stack, player, hand, shop);
                    return ItemInteractionResult.SUCCESS;
                }
            }
            else {
                if(stack.getItem().equals(ModItems.MONEY_POUCH.asItem())){
                    if(!shop.IsShopPriceSet()) {
                        MoveItemFromShopToPlayer(player, shop);
                    }
                    BankDetails details = player.getData(BANKDETAILS);
                    int playerBalance = CurrencyUtil.calculateTotalValue(details.getCopperCoins(), details.getSilverCoins(), details.getGoldCoins(), details.getPinkCoins());
                    int shopPrice = shop.GetShopPrice();
                    if(playerBalance < shopPrice){
                        player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.insufficientFunds"));
                        return ItemInteractionResult.SUCCESS;
                    }

                    if(shop.stockIsEmpty(shop.shopItems.getStackInSlot(31).getCount())){
                        player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.outofStock"));
                        return ItemInteractionResult.SUCCESS;
                    }

                    MoveItemFromShopToPlayer(player, shop);
                    playerBalance -= shopPrice;
                    ItemStack[] coins = CurrencyUtil.convertValueToCoins(playerBalance);
                    shop.UpdateShopProfits(CurrencyUtil.convertValueToCoins(shopPrice));

                    BankDetails updatedDetails = details.update(coins[0].getCount(), coins[1].getCount(), coins[2].getCount(), coins[3].getCount());
                    player.setData(BANKDETAILS, updatedDetails);
                    return ItemInteractionResult.SUCCESS;
                }
            }

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static void MoveItemFromShopToPlayer(Player player, ShopBlockEntity shop) {
        ItemStack referenceStack = shop.shopItems.getStackInSlot(31);
        ItemStack addToPlayer;
        int remaining = referenceStack.getCount();
        for (int i = 0; i < 27; i++) {
            ItemStack slotStack = shop.shopItems.getStackInSlot(i);
            if (slotStack.getItem().equals(referenceStack.getItem())) {
                addToPlayer = shop.shopItems.extractItem(i, remaining, false);
                if(remaining == 0){
                    return;
                }
                else{
                    remaining -= addToPlayer.getCount();
                    player.addItem(addToPlayer);
                }
            }
        }
    }

    private static void InsertStackIntoStock(ItemStack stack, Player player, InteractionHand hand, ShopBlockEntity shop) {
        for (int i = 0; i < 27; i++) {
            ItemStack slotStack = shop.shopItems.getStackInSlot(i);
            if (slotStack.isEmpty() || (slotStack.getItem().equals(stack.getItem()) && slotStack.getCount() < slotStack.getMaxStackSize())) {
                ItemStack remaining = shop.shopItems.insertItem(i, stack.copy(), false);
                if (remaining.isEmpty()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    return;
                } else {
                    stack.setCount(remaining.getCount());
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (direction) {
            case NORTH:
                return makeShape();
            case SOUTH:
                return rotateShape(makeShape(), Direction.SOUTH);
            case WEST:
                return rotateShape(makeShape(), Direction.WEST);
            case EAST:
                return rotateShape(makeShape(), Direction.EAST);
            default:
                return makeShape();
        }
    }

    private VoxelShape makeShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.9375, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);

        return shape;
    }

    private VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (direction.get2DDataValue() + 2) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShopBlockEntity(pos, state);
    }
}
