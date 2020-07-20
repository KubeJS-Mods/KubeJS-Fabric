package dev.latvian.kubejs.bindings;

import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class FacingWrapper {
	public final Direction down;
	public final Direction up;
	public final Direction north;
	public final Direction south;
	public final Direction west;
	public final Direction east;
	public final Map<String, Direction> map;
	
	public FacingWrapper() {
		down = Direction.DOWN;
		up = Direction.UP;
		north = Direction.NORTH;
		south = Direction.SOUTH;
		west = Direction.WEST;
		east = Direction.EAST;
		HashMap<String, Direction> map0 = new HashMap<>();
		
		for (Direction facing : Direction.values()) {
			map0.put(facing.getName(), facing);
		}
		
		map = Collections.unmodifiableMap(map0);
	}
	
	public Direction opposite(Direction facing) {
		return facing.getOpposite();
	}
	
	public int x(Direction facing) {
		return facing.getOffsetX();
	}
	
	public int y(Direction facing) {
		return facing.getOffsetY();
	}
	
	public int z(Direction facing) {
		return facing.getOffsetZ();
	}
	
	public int getIndex(Direction facing) {
		return facing.getId();
	}
	
	public int getHorizontalIndex(Direction facing) {
		return facing.getHorizontal();
	}
	
	public float getYaw(Direction facing) {
		return facing.asRotation();
	}
	
	public float getPitch(Direction facing) {
		return facing == up ? 180F : facing == down ? 0F : 90F;
	}
	
	public Direction rotateY(Direction facing) {
		return facing.rotateYClockwise();
	}
}