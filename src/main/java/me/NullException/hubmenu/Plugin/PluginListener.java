package me.NullException.hubmenu.Plugin;

import java.util.List;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginListener implements Listener {
    private FileConfiguration config = HubMenuMain.instance.getConfig();
    private String nomBoussole = CommonUtils.colorize(config.getConfigurationSection("BoussoleHub").getString("name"));
    private List<String> loreBoussole = ChangeLoreColor(config.getConfigurationSection("BoussoleHub").getStringList("lore"));
    private CustomMenu menu = HubMenuMain.instance.customMenu;
    private FileConfiguration menuConfig = menu.getData();

    public PluginListener() {
    }


    public List<String> ChangeLoreColor(List<String> ls) {
        int index = 0;
        for (String s : ls) {
            ls.set(index, CommonUtils.colorize(s));
            index++;
        }
        return ls;
    }


    @EventHandler
    public void onSaturationLoss(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (config.get("spawnlocation") instanceof Location)
            e.setRespawnLocation((Location) config.get("spawnlocation"));
    }

    @EventHandler
    public void PreventItemMoveAndSwitchPlayer(InventoryClickEvent eClick) {
        if (eClick.getClickedInventory() != null)
            if (eClick.getView().getTitle().equalsIgnoreCase(menuConfig.getString("menu.title"))) {
                if (eClick.getRawSlot() < eClick.getView().getTopInventory().getSize()) {
                    if (HubMenuMain.instance.serverItem.mapServerItem.get(HubMenuMain.instance.customMenu.getInventory().getItem(eClick.getSlot())) != null) {
                        String serveur = HubMenuMain.instance.serverItem.mapServerItem.get(HubMenuMain.instance.customMenu.getInventory().getItem(eClick.getSlot()));
                        Player player = (Player) eClick.getWhoClicked();
                        player.sendMessage(ChatColor.GOLD + "Connection to " + serveur + "...");
                        HubMenuMain.bl.sendPluginMessage("Connect", player,
                                new String[]{serveur});
                    }
                }
                eClick.setCancelled(true);
            }

    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("hubmenu.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        if (config.get("spawnlocation") instanceof Location)
            player.teleport((Location) config.get("spawnlocation"));
        if (!player.isOp()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
        }
        player.setInvulnerable(true);
        ItemStack boussoleHub = new ItemStack(Material.COMPASS, 1);
        ItemMeta metaBH = boussoleHub.getItemMeta();
        metaBH.setDisplayName(nomBoussole);
        metaBH.setLore(loreBoussole);
        boussoleHub.setItemMeta(metaBH);
        player.getInventory().setItem(0, boussoleHub);
        HubMenuMain.instance.serverhud.addPlayer(player);
        if (HubMenuMain.instance.serverPopulation.size() == 0)
            getServerPopulation();
    }

    public void getServerPopulation() {

        new BukkitRunnable() {
            @Override
            public void run() {
                Player yahoo = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                if (HubMenuMain.instance.lstServeurs.size() == 0)
                    HubMenuMain.bl.sendPluginMessage("GetServers", yahoo, new String[]{});
                if (yahoo != null) {
                    for (String s : HubMenuMain.instance.lstServeurs) {
                        HubMenuMain.bl.sendPluginMessage("PlayerCount", yahoo, new String[]{s});
                    }
                }
            }
        }.runTaskTimer(HubMenuMain.instance, 0L, 20L);
    }

    @EventHandler
    public void OnInteract(PlayerInteractEvent event) {

        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        ItemStack itemHand = event.getItem();
        Player player = event.getPlayer();
        if (itemHand != null && itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName()
                && itemHand.getItemMeta().getDisplayName().equalsIgnoreCase(nomBoussole)) {
            HubMenuMain.instance.customMenu.Open(player);
        }
    }

    @EventHandler
    public void OnInventoryClosed(InventoryCloseEvent event) {
        Inventory inv = event.getView().getTopInventory();
        if (event.getView().getTitle().equalsIgnoreCase("editmenu")) {
            HubMenuMain.instance.customMenu.Save(inv);
        }
    }

}
