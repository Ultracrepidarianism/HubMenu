package me.NullException.hubmenu.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ServerItem implements PluginMessageListener {

	private FileConfiguration itemConfig;
	private File itemFile;
	private Plugin main = HubMenuMain.getPlugin(HubMenuMain.class);
	private String path = main.getDataFolder().getAbsolutePath() + File.separator + "ServerItems";
	private File dataFolder = new File(path);
	public Map<ItemStack,String> mapServerItem;

	public ServerItem() {
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		mapServerItem = GetItems();
	}

	public void Setup(String[] listServer) {
		for (String server : listServer) {
			itemFile = new File(dataFolder, server + ".yml");
			if (!itemFile.exists()) {
				try {
					itemFile.createNewFile();
					itemConfig = YamlConfiguration.loadConfiguration(itemFile);
					itemConfig.set("server", server);
					itemConfig.set("item", "");
					itemConfig.save(itemFile);
				} catch (IOException e) {
					HubMenuMain.instance.getLogger().info("Could not add the serverItem File");
					e.printStackTrace();
				}
			}
		}
	}

	public File getPath() {
		return dataFolder;
	}

	public void addItem(ItemStack pItem, String pServer, Player pPlayer) {
		sendGetServers(pPlayer);
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				if(pItem == null)
				{
					pPlayer.sendMessage(ChatColor.RED + "You cannot use this item!");
					return;
				}
				for (ItemStack item : GetItems().keySet()) {
					if (pItem.equals(item)) {
						pPlayer.sendMessage(ChatColor.RED + "This item is already linked to a server !");
						return;
					}
				}
				for (File file : dataFolder.listFiles()) {
					if (file.getName().toLowerCase().contains(pServer.toLowerCase())) {
						itemConfig = YamlConfiguration.loadConfiguration(file);
						itemConfig.set("item", pItem);
						try {
							itemConfig.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
						mapServerItem.put(pItem, pServer);
						pPlayer.sendMessage(ChatColor.GREEN + "Item linked to a server succesfully !\n"
								+ ChatColor.GREEN + "Put it in your menu using /editmenu !");
						pPlayer.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
						return;
					}
				}
				pPlayer.sendMessage("This server doesn't exist !");

			}
		}, 5);

	}

	public Map<ItemStack, String> GetItems() {
		Map<ItemStack, String> listItem = new HashMap<>();
		for (File test : new File(path).listFiles()) {
			itemConfig = YamlConfiguration.loadConfiguration(test);
			ItemStack item = itemConfig.getItemStack("item");
			if (item != null)
				listItem.put(item, itemConfig.getString("server"));
		}
		return listItem;
	}

	public void sendGetServers(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");

		if (player == null)
			player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		player.sendPluginMessage(main, "BungeeCord", out.toByteArray());
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord"))
			return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String sub = in.readUTF();
		if (sub.equals("GetServers")) {
			String[] listServer = in.readUTF().split(", ");
			Setup(listServer);
		}
	}
}
