package vazkii.patchouli.common.util;

import net.minecraft.util.Direction;
import net.minecraft.util.BlockRotation;

public class BlockRotationUtil {

	public static int x(BlockRotation rot, int x, int z) {
		switch(rot) {
		case NONE: return x;
		case CLOCKWISE_180: return -x;
		case CLOCKWISE_90: return z;
		default: return -z;
		}
	}
	
	public static int z(BlockRotation rot, int x, int z) {
		switch(rot) {
		case NONE: return z;
		case CLOCKWISE_180: return -z;
		case CLOCKWISE_90: return x;
		default: return -x;
		}
	}
	
	public static BlockRotation BlockRotationFromFacing(Direction facing) {
		switch(facing) {
		case EAST: return BlockRotation.CLOCKWISE_90;
		case SOUTH: return BlockRotation.CLOCKWISE_180;
		case WEST: return BlockRotation.COUNTERCLOCKWISE_90;
		default: return BlockRotation.NONE;
		}
	}
	
	public static BlockRotation fixHorizontal(BlockRotation rot) {
		switch(rot) {
		case CLOCKWISE_90: return BlockRotation.COUNTERCLOCKWISE_90;
		case COUNTERCLOCKWISE_90: return BlockRotation.CLOCKWISE_90;
		default: return rot;
		}
	}

}
