package me.NullException.hubmenu.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
	FileConfiguration cfg = HubMenuMain.instance.getConfig();
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// label = command args = everything after
		if(label.equalsIgnoreCase("test"))
		{
			HubMenuMain.instance.serverhud.setColor(BarColor.RED);
		}
		if (label.equalsIgnoreCase("editmenu")) {
			if (sender instanceof Player) {
				ServerItem serverItem = HubMenuMain.serverItem;
				CustomMenu customMenu = HubMenuMain.customMenu;
				Player player = (Player) sender;
				if(player.hasPermission("hubmenu.editmenu.use"))
				if (args.length == 0) {
					ItemStack[] actualMenuContent = customMenu.getInventory().getContents();

					Inventory editmenu = Bukkit.createInventory(null, customMenu.getInventory().getSize(), "editmenu");
					List<ItemStack> listItem = serverItem.GetItems();
					for (ItemStack item : listItem)
						player.getInventory().addItem(item);
					editmenu.setContents(actualMenuContent);
					player.openInventory(editmenu);
					return true;
				}
				if (args.length == 1) {
					List<Integer> tableSize = new ArrayList<>(Arrays.asList(9, 18, 27, 36, 45, 54));
					if (!tableSize.contains(Integer.parseInt(args[0]))) {
						player.sendMessage(ChatColor.RED
								+ "Please use one of the following numbers as size : 9, 18, 27, 36, 45, 54");
						return true;
					}
					Inventory editmenu = Bukkit.createInventory(null, Integer.parseInt(args[0]), "editmenu");
					List<ItemStack> listItem = serverItem.GetItems();
					for (ItemStack item : listItem)
						editmenu.addItem(item);
					player.openInventory(editmenu);
					return true;
				}
			}
		}
		
		if (label.equalsIgnoreCase("setspawn"))
		{
			if(sender instanceof Player)
			{
				if(args.length == 0)
				{
				Player player = (Player)sender;
				cfg.set("spawnlocation", player.getLocation());
				HubMenuMain.instance.saveConfig();
				player.sendMessage(ChatColor.GREEN + "Spawn successfully set!");
				return true;
				}
			}
		}
		if(label.equalsIgnoreCase("spawn"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				if((Location)cfg.get("spawnlocation") instanceof Location)
				{
				Location spawn = (Location)cfg.get("spawnlocation");
				player.teleport(spawn);
				return true;
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Please set the spawn first!");
					return true;
				}
			}

		}
		if(label.equalsIgnoreCase("Fly"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				if(player.hasPermission("hubmenu.fly"))
				{
					player.setAllowFlight(!player.getAllowFlight());
					player.setFlying(player.getAllowFlight());
					String message = player.getAllowFlight() == true ? ChatColor.GREEN + "Fly enabled!": ChatColor.RED + "Fly disabled!";
					player.sendMessage(message);
					return true;
				}
			}
		}
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("hubmenu.serveritem.modify")) {
				ItemStack item = player.getInventory().getItemInMainHand();
				ItemMeta itemM = item.getItemMeta();
				if (label.equalsIgnoreCase("setdisplayname")) {

					if (args.length >= 1) {
						String displayname = "";
						for (String addSpace : args) {
							if (addSpace.equalsIgnoreCase(args[0]))
								displayname = addSpace;
							else
								displayname += (" " + addSpace);
						}
						itemM.setDisplayName(displayname.replaceAll("&", "§"));
						item.setItemMeta(itemM);
						player.getInventory().setItemInMainHand(item);
						return true;
					}
				}
				if (label.equalsIgnoreCase("setlore")) {
					if (args.length >= 2) {
						String lore = "";
						for (int i = 1; i < args.length; i++) {
							if (i == 1)
								lore = args[i];
							else
								lore += (" " + args[i]);
						}
						List<String> listLore = new ArrayList<String>();
						if (itemM.hasLore())
							listLore = itemM.getLore();

						while (listLore.size() < (Integer.parseInt(args[0]))) {
							listLore.add("");
						}
						listLore.set((Integer.parseInt(args[0]) - 1), lore.replaceAll("&", "§"));
						itemM.setLore(listLore);
						item.setItemMeta(itemM);
						player.getInventory().setItemInMainHand(item);
						return true;
					}
				}
				if (label.equalsIgnoreCase("setenchant")) {
					if (args.length == 2) {
						Enchantment enchant = Enchantment.getByName(args[0].toUpperCase());
						if (enchant != null) {
							itemM.addEnchant(enchant, Integer.parseInt(args[1]), true);
							item.setItemMeta(itemM);
							player.sendMessage(ChatColor.GREEN + "The enchant was succesfully added to the item!");
							return true;
						} else {
							String line = ChatColor.RED + "Please use one of the following enchants for your item: \n";
							for (Enchantment itemEnchant : Enchantment.values()) {
								if (!itemEnchant.equals(Enchantment.values()[Enchantment.values().length - 1]))
									line += itemEnchant.getName() + ", ";
								else
									line += itemEnchant.getName();
							}

							player.sendMessage(line);
						}
					}
				}
				if (label.equalsIgnoreCase("setitemflag")) {
					if (args.length == 1) {
						String line = ChatColor.RED + "Please use one of the following itemflags for your item: \n";
						for (ItemFlag itemflag : ItemFlag.values()) {
							if (itemflag.name().equalsIgnoreCase(args[0])) {
								itemM.addItemFlags(itemflag);
								item.setItemMeta(itemM);
								player.sendMessage(ChatColor.GREEN + "The itemflag was succesfully added to the item!");
								return true;
							}
							if (!itemflag.equals(ItemFlag.values()[ItemFlag.values().length - 1]))
								line += itemflag.name() + ", ";
							else
								line += itemflag.name();
						}
						player.sendMessage(line);
					}
				}
				if (label.equalsIgnoreCase("addserveritem")) {
					ServerItem serverItem = HubMenuMain.serverItem;
					if (args.length == 1) {
						serverItem.addItem(player.getInventory().getItemInMainHand(), args[0], player);
						return true;
					}
				}
			}
		} else {
			HubMenuMain.instance.getLogger().info(ChatColor.RED + "You must be a player to use this command");
			return true;
		}
		return false;
	}
}
