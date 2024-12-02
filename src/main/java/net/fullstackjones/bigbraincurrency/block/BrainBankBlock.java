package net.fullstackjones.bigbraincurrency.block;

import net.fullstackjones.bigbraincurrency.data.BrainBankData;
import net.fullstackjones.bigbraincurrency.entities.BrainBankBlockEntity;
import net.fullstackjones.bigbraincurrency.entities.ShopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BrainBankBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BrainBankBlock(Properties props) {
        super(props);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrainBankBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return EntityBlock.super.getTicker(level, state, blockEntityType);
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
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0, 1, 0.125, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.9375, 1, 0.125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, -0.0625, 1, 0.125, 0), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.8125, 0.0625, -0.0625, 0.0625, 0.125, 0), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.25, 0.1875, 0.4375, 0.6875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.625, 0.625, 0.25, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.25, 0.1875, 0.875, 0.6875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.25, 0.25, 0.5625, 0.625, 0.75), BooleanOp.OR);

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
