package ftgumod.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class LootUtils {

	private static final Field field = ReflectionHelper.findField(LootTable.class, "pools", "field_186466_c");

	public static void addLootPools(LootTableManager manager, LootTable table, ResourceLocation pools) {
		LootTable extra = manager.getLootTableFromLocation(pools);
		try {
			//noinspection unchecked
			for (LootPool pool : (List<LootPool>) field.get(extra))
				table.addPool(pool);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
