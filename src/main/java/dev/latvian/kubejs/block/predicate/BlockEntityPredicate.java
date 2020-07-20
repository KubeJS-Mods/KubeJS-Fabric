package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author LatvianModder
 */
public class BlockEntityPredicate implements BlockPredicate
{
	private final Identifier id;
	private BlockEntityPredicateDataCheck checkData;

	public BlockEntityPredicate(@ID String i)
	{
		id = UtilsJS.getMCID(i);
	}

	public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd)
	{
		checkData = cd;
		return this;
	}

	@Override
	public boolean check(BlockContainerJS block)
	{
		BlockEntity blockEntity = block.getEntity();
		return blockEntity != null && id.equals(Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType())) && (checkData == null || checkData.checkData(block.getEntityData()));
	}

	@Override
	public String toString()
	{
		return "{entity=" + id + "}";
	}
}