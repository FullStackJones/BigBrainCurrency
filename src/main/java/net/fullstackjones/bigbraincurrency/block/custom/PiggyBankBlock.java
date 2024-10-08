package net.fullstackjones.bigbraincurrency.block.custom;


import net.fullstackjones.bigbraincurrency.Config;
import net.fullstackjones.bigbraincurrency.data.BankDetails;
import net.fullstackjones.bigbraincurrency.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static net.fullstackjones.bigbraincurrency.data.ModAttachmentTypes.BANKDETAILS;

public class PiggyBankBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public PiggyBankBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide){
            BankDetails details = player.getData(BANKDETAILS);
            //if(true)
            if(details.getUBI(LocalDateTime.now()))
            {
                getPlayerAllowance().forEach((key, value) -> {
                    ItemStack coin = getCoin(key);
                    coin.setCount(value);
                    if(!player.getInventory().add(coin)){
                        player.drop(coin, false);
                    }
                });
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 2.0F, 1.0F);
                player.setData(BANKDETAILS, details.update(LocalDateTime.now()));
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    private Map<String, Integer> getPlayerAllowance(){
        Map<String, Integer> allowances = new HashMap<>();
        allowances.put("pink", Config.PINK_UBI_ALLOWANCE.getDefault());
        allowances.put("gold", Config.GOLD_UBI_ALLOWANCE.getDefault());
        allowances.put("silver", Config.SILVER_UBI_ALLOWANCE.getDefault());
        allowances.put("copper", Config.COPPER_UBI_ALLOWANCE.getDefault());
        return allowances;
    }
    private ItemStack getCoin(String coinType){
        switch(coinType){
            case "pink":
                return ModItems.PINKCOIN.get().getDefaultInstance();
            case "gold":
                return ModItems.GOLDCOIN.get().getDefaultInstance();
            case "silver":
                return ModItems.SILVERCOIN.get().getDefaultInstance();
            case "copper":
                return ModItems.COPPERCOIN.get().getDefaultInstance();
            default:
                return ModItems.COPPERCOIN.get().getDefaultInstance();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
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
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.0625, 0.8125, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.375, 0.625, 0.25, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.25, 0.25, 0.5625, 0.5625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.1875, 0.25, 0.75, 0.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.25, 0.25, 0.875, 0.5625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.25, 0.1875, 0.8125, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.25, 0.25, 0.1875, 0.5625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.25, 0.1875, 0.4375, 0.625, 0.6875), BooleanOp.OR);
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

}
