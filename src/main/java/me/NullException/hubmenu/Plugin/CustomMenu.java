package me.NullException.hubmenu.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
            }
        } else {
            Reload();
            return;
        }

        invConfig = YamlConfiguration.loadConfiguration(invFile);
        invConfig.set("menu.title", "Lobby Selector");
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
        if (inventory != null) {
            title = invConfig.getString("menu.title");
            invFile.delete();
            try {
                invFile.createNewFile();
            } catch (IOException e) {
                HubMenuMain.instance.getLogger().info("Couldn't create the new invFile");
                e.printStackTrace();
            }
            invConfig = YamlConfiguration.loadConfiguration(invFile);
            invConfig.set("menu.title", title);
            invConfig.set("menu.size", inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack saveItem = inventory.getItem(i);
                if (saveItem != null)
                    invConfig.set("menu.items." + i, saveItem);
            }
        }


        try {
            invConfig.save(invFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
        }
        Reload();
    }

    public void Reload() {
        invConfig = YamlConfiguration.loadConfiguration(invFile);
        title = invConfig.getString("menu.title");
        menu = Bukkit.createInventory(null, invConfig.getInt("menu.size"));
        for (int i = 0; i < invConfig.getInt("menu.size"); i++) {
            ItemStack loadItem = invConfig.getItemStack("menu.items." + i);
            if (loadItem != null) {
                menu.setItem(i, loadItem);
            }
        }
    }

    public void Open(Player player) {
        Inventory temp = Bukkit.createInventory(null, menu.getSize(), title);
        temp.setContents(menu.getContents());
        temp = ReplaceTags(temp);
        player.openInventory(temp);
    }

    public Inventory ReplaceTags(Inventory inv) {

        for (ItemStack is : inv.getContents()) {
            if (is != null) {
                ItemMeta im = is.hasItemMeta() ? is.getItemMeta() : null;
                if (im != null) {
                    if (im.hasDisplayName() && im.getDisplayName().contains("%playercount%")) {
                        im.setDisplayName(im.getDisplayName().replaceAll("%playercount%", getPlayerCount(is)));
                    }
                    if(im.hasLore()){
                        int index= 0;
                        System.out.println("Live and let live");
                        List<String> lstLore = im.getLore();
                        for(String line : lstLore){
                            System.out.println();
                            if(line.contains("%playercount%"))
                                System.out.println();
                                lstLore.set(index, line.replaceAll("%playercount%", getPlayerCount(is)));
                            index++;
                        }
                        im.setLore(lstLore);
                    }
                    is.setItemMeta(im);
                }
            }
        }
        return inv;
    }


    private String getPlayerCount(ItemStack i) {
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		String serverName = "";
        ServerItem si = HubMenuMain.instance.serverItem;
        for (ItemStack is : si.GetItems().keySet()) {
            if (is.equals(i)) {
                serverName = si.GetItems().get(i);
            }
        }

        if(serverName.equals("")) return "Offline";

        return HubMenuMain.instance.serverPopulation.get(serverName) == null ? "Error Loading" : HubMenuMain.instance.serverPopulation.get(serverName).toString();
    }


    public Inventory getInventory() {
        return menu;
    }
}

