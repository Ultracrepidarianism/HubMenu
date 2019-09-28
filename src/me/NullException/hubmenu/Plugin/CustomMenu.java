package me.NullException.hubmenu.Plugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CustomMenu {

	private FileConfiguration invConfig;
	private String title;
	private File invFile;
	private Inventory menu;
	
	public CustomMenu() {
		Setup(HubMenuMain.getPlugin(HubMenuMain.class));
	}

	public void Setup(Plugin p) {
		
		invFile = new File(p.getDataFolder(), "customChest.yml");

		if (!invFile.exists()) {
			try {
				invFile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
			}
		}
		else {
			invConfig = YamlConfiguration.loadConfiguration(invFile);
			return;
		}

		invConfig = YamlConfiguration.loadConfiguration(invFile);
		invConfig.set("menu.title","Lobby Selector");
		invConfig.createSection("menu.size");
		invConfig.set("menu.size", 9);
		invConfig.createSection("menu.items");
		Save(null);
		Reload();
	}

	public FileConfiguration getData() {
		return invConfig;
	}

	public void Save(Inventory inventory) {
		if(inventory != null)
		{
			title = invConfig.getString("menu.title");
			invFile.delete();
			try {
				invFile.createNewFile();
			} catch (IOException e) {
				HubMenuMain.instance.getLogger().info("Couldn't create the new invFile");
				e.printStackTrace();
			}
			invConfig = YamlConfiguration.loadConfiguration(invFile);
			invConfig.set("menu.title",title);
			invConfig.set("menu.size",inventory.getSize());
			for(int i = 0 ; i < inventory.getSize() ; i++)
			{
				ItemStack saveItem = inventory.getItem(i);
				if(saveItem != null)
				invConfig.set("menu.items." + i, saveItem);
			}
		}

		
		try {
			invConfig.save(invFile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
		}
		Reload();
	}

	public void Reload() {
		invConfig = YamlConfiguration.loadConfiguration(invFile);
		menu = Bukkit.createInventory(null, invConfig.getInt("menu.size"), invConfig.getString("menu.title"));
		
		for(int i= 0; i < invConfig.getInt("menu.size"); i++)
		{
				ItemStack loadItem = invConfig.getItemStack("menu.items." + i);
				if(loadItem != null)
				{
				menu.setItem(i, loadItem);
				}
		}
	}

	public void Open(Player player)
	{
		
		player.openInventory(menu);
	}
	
	public Inventory getInventory()
	{
		return menu;
	}
}

