package me.NullException.hubmenu.Plugin;

import java.io.File;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class PluginListener implements Listener {
	private FileConfiguration config = HubMenuMain.instance.getConfig();
	private String nomBoussole = config.getConfigurationSection("BoussoleHub").getString("name").replaceAll("&", "§");
	private List<String> loreBoussole = config.getConfigurationSection("BoussoleHub").getStringList("lore");
	private CustomMenu menu = HubMenuMain.customMenu;

	@EventHandler
	public void onSaturationLoss(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("hubmenu.fly")) {
			player.setAllowFlight(true);
			player.setFlying(true);
		}
		if((Location)config.get("spawnlocation") instanceof Location)
			player.teleport((Location) config.get("spawnlocation"));
		if (!player.isOp()) {
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.setInvulnerable(true);
		}
		ItemStack boussoleHub = new ItemStack(Material.COMPASS, 1);
		ItemMeta metaBH = boussoleHub.getItemMeta();
		metaBH.setDisplayName(config.getConfigurationSection("BoussoleHub").getString("name").replaceAll("&", "§"));
		metaBH.setLore(loreBoussole);
		boussoleHub.setItemMeta(metaBH);
		player.getInventory().setItem(0, boussoleHub);
		HubMenuMain.instance.serverhud.addPlayer(player);
	}

	@EventHandler
	public void OnInteract(PlayerInteractEvent event) {

		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		ItemStack itemHand = event.getItem();
		Player player = event.getPlayer();
		if (itemHand != null && itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName()
				&& itemHand.getItemMeta().getDisplayName().equalsIgnoreCase(nomBoussole)) {
			player.openInventory(menu.getInventory());
		}
	}

	@EventHandler
	public void OnInventoryClosed(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();
		if (inv.getName() == "editmenu") {
			menu.Save(inv);
		}
	}

	@EventHandler
	public void AntiMoveAndTp(InventoryClickEvent eClick) {
		ServerItem serverItem = HubMenuMain.serverItem;
		File dataFolder = serverItem.getPath();
		FileConfiguration itemConfig;
		if (eClick.getClickedInventory() != null)
			if (!eClick.getView().getTopInventory().getTitle().equalsIgnoreCase("editmenu")) {
				if (dataFolder.isDirectory() && dataFolder.listFiles().length > 0) {
					for (File item : dataFolder.listFiles()) {
						itemConfig = YamlConfiguration.loadConfiguration(item);
						if (eClick.getCurrentItem().equals(itemConfig.getItemStack("item"))) {
							if (eClick.getWhoClicked().hasPermission("hubmenu.inventory.move")
									&& !eClick.isShiftClick())
								return;
							Player player = (Player) eClick.getWhoClicked();
							player.sendMessage(
									ChatColor.GOLD + "Connection to " + itemConfig.getString("server") + "...");
							BungeeListener.sendPluginMessage("Connect", player,
									new String[] { itemConfig.getString("server") });
							eClick.setCancelled(true);
						}
					}
				}

				if (eClick.getWhoClicked().hasPermission("hubmenu.inventory.move"))
					return;

				eClick.setCancelled(true);
			}

	}

//	public int getPlayers(ItemStack pItem, Player pPlayer)
//	{
//		ServerItem serverItem = new ServerItem();
//		File dataFolder = serverItem.getPath();
//		FileConfiguration itemConfig;
//		List<ItemStack> listItem = new ArrayList<ItemStack>();
//		for(File item : dataFolder.listFiles())
//		{
//			itemConfig = YamlConfiguration.loadConfiguration(item);
//			if(pItem.equals(itemConfig.getItemStack("item")))
//			{
//				String server = itemConfig.getString("server");
//			}
//			
//		}
//		return 0;
//	}

}
