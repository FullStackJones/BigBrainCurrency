package net.fullstackjones.bigbraincurrency.block.custom;

import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
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
import org.jetbrains.annotations.Nullable;

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
       if(level.getBlockEntity(pos) instanceof ShopBlockEntity shop){
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!level.isClientSide) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(shop, Component.literal("shop")), pos);

            }
        }
        return InteractionResult.SUCCESS;
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
