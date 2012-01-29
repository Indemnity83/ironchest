package cpw.mods.ironchest;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraft.src.forge.Configuration;

public enum IronChestType {
	IRON(54, 9, true, "Iron Chest", null, "ironchest.png", 0, Item.ingotIron, TileEntityIronChest.class, "mmmmPmmmm","mGmG3GmGm"), 
	GOLD(81, 9, true, "Gold Chest", "guiGoldChest", "goldchest.png", 1, Item.ingotGold, TileEntityGoldChest.class, "mmmmPmmmm","mGmG4GmGm"), 
	DIAMOND(108, 12, true, "Diamond Chest", "guiDiamondChest", "diamondchest.png", 2, Item.diamond, TileEntityDiamondChest.class, "GGGmPmGGG", "GGGG4Gmmm"), 
	COPPER(45, 9, false, "Copper Chest", "guiCopperChest", "copperchest.png", 3, null, TileEntityCopperChest.class, "mmmmCmmmm"), 
	SILVER(72, 9, false, "Silver Chest", "guiSilverChest", "silverchest.png", 4, null, TileEntitySilverChest.class, "mmmm0mmmm", "mmmm3mmmm");
	int size;
	private int rowLength;
	String friendlyName;
	private boolean tieredChest;
	private String modelTexture;
	private String guiName;
	private int textureRow;
	private Class<? extends TileEntityIronChest> clazz;
	private Item mat;
	private String[] recipes;
	private int guiId;

	IronChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String guiName, String modelTexture, int textureRow, Item mat,
			Class<? extends TileEntityIronChest> clazz, String... recipes) {
		this.size = size;
		this.rowLength = rowLength;
		this.tieredChest = tieredChest;
		this.friendlyName = friendlyName;
		this.guiName = guiName;
		this.modelTexture = "/cpw/mods/ironchest/sprites/" + modelTexture;
		this.textureRow = textureRow;
		this.clazz = clazz;
		this.mat = mat;
		this.recipes = recipes;
	}

	public String getModelTexture() {
		return modelTexture;
	}

	public int getTextureRow() {
		return textureRow;
	}

	public static TileEntity makeEntity(int metadata) {
		// Compatibility
		int chesttype = metadata;
		try {
			TileEntityIronChest te = values()[chesttype].clazz.newInstance();
			return te;
		} catch (InstantiationException e) {
			// unpossible
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// unpossible
			e.printStackTrace();
		}
		return null;
	}

	public static void registerTileEntities(Class<? extends TileEntitySpecialRenderer> renderer) {
		for (IronChestType typ : values()) {
			try {
				if (renderer != null) {
					ModLoader.RegisterTileEntity(typ.clazz, typ.name(), renderer.newInstance());
				} else {
					ModLoader.RegisterTileEntity(typ.clazz, typ.name());
				}
			} catch (InstantiationException e) {
				// unpossible
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// unpossible
				e.printStackTrace();
			}
		}
	}

	public static void registerTranslations() {
		for (IronChestType typ : values()) {
			ModLoader.AddLocalization(typ.name() + ".name", typ.friendlyName);
		}
	}

	public static void generateTieredRecipies(BlockIronChest blockResult) {
		ItemStack previous = new ItemStack(Block.chest);
		for (IronChestType typ : values()) {
			if (!typ.tieredChest)
				continue;
			generateRecipesForType(blockResult, previous, typ, typ.mat);
			previous = new ItemStack(blockResult, 1, typ.ordinal());
		}
	}

	public static void generateRecipesForType(BlockIronChest blockResult, Object previousTier, IronChestType type, Object mat) {
		for (String recipe : type.recipes) {
			String[] recipeSplit = new String[] { recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9) };
			addRecipe(new ItemStack(blockResult, 1, type.ordinal()), recipeSplit, 'm', mat, 'P', previousTier, 'G', Block.glass, 'C', Block.chest,
					'0', new ItemStack(blockResult, 1, 0)/* Iron */, '1', new ItemStack(blockResult, 1, 1)/* GOLD */, '3', new ItemStack(blockResult,
							1, 3)/* Copper */, '4', new ItemStack(blockResult,1,4));
		}
	}

	private static void addRecipe(ItemStack is, Object... parts) {
		ModLoader.AddRecipe(is, parts);
	}

	public int getGUI() {
		return guiId;
	}

	public static void initGUIs(Configuration cfg) {
		int defGUI = 51;
		for (IronChestType typ : values()) {
			if (typ.guiName != null) {
				typ.guiId = Integer.parseInt(cfg.getOrCreateIntProperty(typ.guiName, Configuration.GENERAL_PROPERTY, defGUI++).value);
			} else {
				typ.guiId = -1;
			}
		}
	}

	public int getRowCount() {
		return size / rowLength;
	}

	public int getRowLength() {
		// TODO Auto-generated method stub
		return rowLength;
	}

}