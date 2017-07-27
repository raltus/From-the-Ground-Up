package ftgumod.client;

import ftgumod.ItemList;
import ftgumod.technology.TechnologyUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ItemListClient extends ItemList {

	public ItemListClient(Object obj) {
		super(obj);

		for (int i = 0; i < list.size(); i++) {
			ItemStack stack = list.get(i);
			if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
				Item item = stack.getItem();
				for (CreativeTabs tab : item.getCreativeTabs())
					stack.getItem().getSubItems(tab, list);

				list.remove(stack);
				i--;
			} else if (!forced && !TechnologyUtil.hasRecipe(stack)) {
				list.remove(stack);
				i--;
			}
		}

		if (obj instanceof String && !list.isEmpty() && !I18n.hasKey(name + ".name"))
			name = list.get(0).getUnlocalizedName();
	}

	public int size() {
		return list.size();
	}

	public ItemStack get(int index) {
		return list.get(index);
	}

	public List<ItemStack> getRaw() {
		return list;
	}

}
