package cz.neumimto.rpg.gui;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by fs on 9.10.17.
 */
public abstract class CatalogTypeItemStackBuilder {

	public abstract ItemStack toItemStack();

	public static class Block extends CatalogTypeItemStackBuilder {

		private BlockType blockType;

		public static CatalogTypeItemStackBuilder of(BlockType type) {
			Block block = new Block();
			block.blockType = type;
			return block;
		}

		@Override
		public ItemStack toItemStack() {
			return ItemStack.of(blockType.getItem().get(), 1);
		}
	}

	public static class Item extends CatalogTypeItemStackBuilder {

		private ItemType itemType;

		public static CatalogTypeItemStackBuilder of(ItemType type) {
			Item block = new Item();
			block.itemType = type;
			return block;
		}

		@Override
		public ItemStack toItemStack() {
			return ItemStack.of(itemType, 1);
		}
	}


}
