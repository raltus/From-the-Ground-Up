package ftgumod;

import ftgumod.technology.TechnologyHandler.ITEM_GROUP;
import ftgumod.technology.TechnologyUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;

public class ItemList implements Iterable<ItemStack> {

	protected final NonNullList<ItemStack> list = NonNullList.create();
	protected final boolean forced;
	protected String name;

	public ItemList() {
		name = "null";
		forced = false;
	}

	public ItemList(Object obj) {
		this(obj, false);
	}

	public ItemList(Object obj, boolean forced) {
		name = TechnologyUtil.toString(obj);

		if (obj instanceof ItemStack)
			list.add((ItemStack) obj);
		else if (obj instanceof String)
			list.addAll(OreDictionary.getOres((String) obj));
		else if (obj instanceof Item)
			list.add(new ItemStack((Item) obj, 1, OreDictionary.WILDCARD_VALUE));
		else if (obj instanceof Block)
			list.add(new ItemStack((Block) obj, 1, OreDictionary.WILDCARD_VALUE));
		else if (obj instanceof ITEM_GROUP)
			for (ItemList l : ((ITEM_GROUP) obj).item)
				list.addAll(l.list);
		else if (obj instanceof ItemList) {
			ItemList item = (ItemList) obj;
			list.addAll(item.list);
			forced = item.forced;
		}

		this.forced = forced;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean contains(ItemStack item) {
		if (isEmpty() && item.isEmpty())
			return true;
		for (ItemStack s : list)
			if (OreDictionary.itemMatches(s, item, false) && (!s.hasTagCompound() || ItemStack.areItemStackTagsEqual(s, item)))
				return true;
		return false;
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return list.iterator();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

}
