package vazkii.patchouli.common.multiblock;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.TriPredicate;
import vazkii.patchouli.common.util.RotationUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMultiblock implements IMultiblock, BlockView {
    public Identifier res;
    protected int offX, offY, offZ;
    protected int viewOffX, viewOffY, viewOffZ;
    private boolean symmetrical;
    World world;

    private final transient Map<BlockPos, BlockEntity> teCache = new HashMap<>();

    @Override
    public IMultiblock offset(int x, int y, int z) {
        return setOffset(offX + x, offY + y, offZ + z);
    }

    public IMultiblock setOffset(int x, int y, int z) {
        offX = x;
        offY = y;
        offZ = z;
        return setViewOffset(x, y, z);
    }

    void setViewOffset() {
        setViewOffset(offX, offY, offZ);
    }

    @Override
    public IMultiblock offsetView(int x, int y, int z) {
        return setViewOffset(viewOffX + x, viewOffY + y, viewOffZ + z);
    }

    public IMultiblock setViewOffset(int x, int y, int z) {
        viewOffX = x;
        viewOffY = y;
        viewOffZ = z;
        return this;
    }

    @Override
    public IMultiblock setSymmetrical(boolean symmetrical) {
        this.symmetrical = symmetrical;
        return this;
    }

    @Override
    public Identifier getID() {
        return res;
    }

    @Override
    public IMultiblock setIdentifier(Identifier res) {
        this.res = res;
        return this;
    }

    @Override
    public void place(World world, BlockPos pos, BlockRotation rotation) {
        setWorld(world);
        simulate(world, pos, rotation, false).getSecond().forEach(r -> {
            BlockPos placePos = r.getWorldPosition();
            BlockState targetState = r.getStateMatcher().getDisplayedState((int) world.getTimeOfDay()).rotate(rotation);
            Block targetBlock = targetState.getBlock();

            if(!targetState.isAir() && targetState.canPlaceAt(world, placePos) && world.getBlockState(placePos).getMaterial().isReplaceable())
                world.setBlockState(placePos, targetState);
        });
    }

    @Override
    public BlockRotation validate(World world, BlockPos pos) {
        if (isSymmetrical() && validate(world, pos, BlockRotation.NONE))
            return BlockRotation.NONE;
        else for (BlockRotation rot : BlockRotation.values()) {
            if(validate(world, pos, rot)) {
                return rot;
            }
        }
        return null;
    }

    @Override
    public boolean validate(World world, BlockPos pos, BlockRotation rotation) {
        setWorld(world);
        Pair<BlockPos, Collection<SimulateResult>> sim = simulate(world, pos, rotation, false);

        return sim.getSecond().stream().allMatch(r -> {
            BlockPos checkPos = r.getWorldPosition();
            TriPredicate<BlockView, BlockPos, BlockState> pred = r.getStateMatcher().getStatePredicate();
            BlockState state = world.getBlockState(checkPos).rotate(RotationUtil.fixHorizontal(rotation));

            return pred.test(world, checkPos, state);
        });
    }

    @Override
    public boolean isSymmetrical() {
        return symmetrical;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }

    //@Override
    public Biome getBiome(BlockPos pos) {
        return Biomes.PLAINS;
    }

    //@Override
    public int getLightFor(LightType type, BlockPos pos) {
        return 0xF000F0;
    }

    public abstract Vec3i getSize();
}
