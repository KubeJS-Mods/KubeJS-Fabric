package dev.latvian.kubejs.world;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

/**
 * @author LatvianModder
 */
public class ExplosionJS
{
	private final WorldAccess world;
	public final double x, y, z;
	public EntityJS exploder;
	public float strength;
	public boolean causesFire;
	public Explosion.DestructionType explosionMode;

	public ExplosionJS(WorldAccess w, double _x, double _y, double _z)
	{
		world = w;
		x = _x;
		y = _y;
		z = _z;
		exploder = null;
		strength = 3F;
		causesFire = false;
		explosionMode = Explosion.DestructionType.BREAK;
	}

	public ExplosionJS exploder(EntityJS entity)
	{
		exploder = entity;
		return this;
	}

	public ExplosionJS strength(float f)
	{
		strength = f;
		return this;
	}

	public ExplosionJS causesFire(boolean b)
	{
		causesFire = b;
		return this;
	}

	public ExplosionJS damagesTerrain(boolean b)
	{
		explosionMode = b ? Explosion.DestructionType.BREAK : Explosion.DestructionType.NONE;
		return this;
	}

	public ExplosionJS destroysTerrain(boolean b)
	{
		explosionMode = b ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
		return this;
	}

	public void explode()
	{
		if (world instanceof World)
		{
			((World) world).createExplosion(exploder == null ? null : exploder.minecraftEntity, x, y, z, strength, causesFire, explosionMode);
		}
	}
}