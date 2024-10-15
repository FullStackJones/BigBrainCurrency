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
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof ShopBlockEntity shop) {
                var shopOwner = shop.getOwnerUUID();
                var playerUUID = player.getUUID();
                if (shopOwner.equals(playerUUID)) {
                    if(stack.getItem().equals(ModItems.MONEY_POUCH.toStack().getItem())){
                        var pink = shop.shopItems.getStackInSlot(27);
                        var gold = shop.shopItems.getStackInSlot(28);
                        var silver = shop.shopItems.getStackInSlot(29);
                        var copper = shop.shopItems.getStackInSlot(30);
                        if(pink.isEmpty() && gold.isEmpty() && silver.isEmpty() && copper.isEmpty()){
                            return ItemInteractionResult.SUCCESS;
                        }
                        BankDetails details = player.getData(BANKDETAILS);
                        int playerBalance = CurrencyUtil.calculateTotalValue(details.getCopperCoins(), details.getSilverCoins(), details.getGoldCoins(), details.getPinkCoins());
                        int ShopBalance = CurrencyUtil.calculateTotalValue(copper.getCount(), silver.getCount(), gold.getCount(), pink.getCount());
                        if(playerBalance == CurrencyUtil.getMaxValue()){
                            player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.pouchFull"));
                            return ItemInteractionResult.SUCCESS;
                        }

                        int calculatedBalance = playerBalance + ShopBalance;
                        if(calculatedBalance > CurrencyUtil.getMaxValue()){
                            playerBalance = CurrencyUtil.getMaxValue();
                        }
                        else{
                            playerBalance = calculatedBalance;
                        }

                        int remainingBalance = calculatedBalance - CurrencyUtil.getMaxValue();
                        ItemStack[] remainingCoins = CurrencyUtil.convertValueToCoins(remainingBalance);
                        shop.clearProfit();

                        shop.shopItems.insertItem(30, remainingCoins[0], false);
                        shop.shopItems.insertItem(29, remainingCoins[1], false);
                        shop.shopItems.insertItem(28, remainingCoins[2], false);
                        shop.shopItems.insertItem(27, remainingCoins[3], false);

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
                    if(stack == ModItems.MONEY_POUCH.toStack()){
                        var pink = shop.shopItems.getStackInSlot(32);
                        var gold = shop.shopItems.getStackInSlot(33);
                        var silver = shop.shopItems.getStackInSlot(34);
                        var copper = shop.shopItems.getStackInSlot(35);
                        if(pink.isEmpty() && gold.isEmpty() && silver.isEmpty() && copper.isEmpty()) {
                            MoveItemFromShopToPlayer(player, shop);
                        }
                        BankDetails details = player.getData(BANKDETAILS);
                        int playerBalance = CurrencyUtil.calculateTotalValue(details.getCopperCoins(), details.getSilverCoins(), details.getGoldCoins(), details.getPinkCoins());
                        int shopPrice = CurrencyUtil.calculateTotalValue(copper.getCount(), silver.getCount(), gold.getCount(), pink.getCount());
                        if(playerBalance < shopPrice){
                            player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.insufficientFunds"));
                            return ItemInteractionResult.SUCCESS;
                        }
                        if(MoveItemFromShopToPlayer(player, shop)){

                            playerBalance -= shopPrice;
                            ItemStack[] coins = CurrencyUtil.convertValueToCoins(playerBalance);

                            shop.shopItems.insertItem(30, coins[0], false);
                            shop.shopItems.insertItem(29, coins[1], false);
                            shop.shopItems.insertItem(28, coins[2], false);
                            shop.shopItems.insertItem(27, coins[3], false);
                            BankDetails updatedDetails = details.update(coins[0].getCount(), coins[1].getCount(), coins[2].getCount(), coins[3].getCount());
                            player.setData(BANKDETAILS, updatedDetails);
                            return ItemInteractionResult.SUCCESS;
                        }

                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static boolean MoveItemFromShopToPlayer(Player player, ShopBlockEntity shop) {
        ItemStack referenceStack = shop.shopItems.getStackInSlot(31);
        var removalSlot = 1;
        for (int i = 0; i < 27; i++) {
            ItemStack slotStack = shop.shopItems.getStackInSlot(i);
            if (slotStack.equals(referenceStack) && slotStack.getCount() == referenceStack.getCount()) {
                removalSlot =  i; // Return the index of the matching slot
            }
        }
        if(removalSlot == 26 && shop.shopItems.getStackInSlot(26).isEmpty()){
            player.sendSystemMessage(Component.translatable("shop.bigbraincurrency.outofStock"));
            return false;
        }
        ItemStack itemstack = shop.shopItems.extractItem(removalSlot, referenceStack.getCount(), false);
        player.addItem(itemstack);
        return true;
    }

    private static void InsertStackIntoStock(ItemStack stack, Player player, InteractionHand hand, ShopBlockEntity shop) {
        for (int i = 0; i < 27; i++) {
            ItemStack slotStack = shop.shopItems.getStackInSlot(i);
            if (slotStack.isEmpty() || (slotStack.equals(stack) && slotStack.getCount() < slotStack.getMaxStackSize())) {
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
