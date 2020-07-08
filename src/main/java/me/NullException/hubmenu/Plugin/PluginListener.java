package me.NullException.hubmenu.Plugin;

import java.io.File;
import java.util.List;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
    private String nomBoussole = config.getConfigurationSection("BoussoleHub").getString("name");
    private List<String> loreBoussole = config.getConfigurationSection("BoussoleHub").getStringList("lore");
    private CustomMenu menu = HubMenuMain.instance.customMenu;
    private FileConfiguration menuConfig = menu.getData();

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
                    if (HubMenuMain.serverItem.mapServerItem.get(HubMenuMain.instance.customMenu.getInventory().getItem(eClick.getSlot())) != null) {
                        String serveur = HubMenuMain.serverItem.mapServerItem.get(HubMenuMain.instance.customMenu.getInventory().getItem(eClick.getSlot()));
                        Player player = (Player) eClick.getWhoClicked();
                        player.sendMessage(ChatColor.GOLD + "Connection to " + serveur + "...");
                        BungeeListener.sendPluginMessage("Connect", player,
                                new String[]{serveur});
                        eClick.setCancelled(true);
                    }
            }

    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getServerPopulation(player);

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
        metaBH.setDisplayName(config.getConfigurationSection("BoussoleHub").getString("name"));
        metaBH.setLore(loreBoussole);
        boussoleHub.setItemMeta(metaBH);
        player.getInventory().setItem(0, boussoleHub);
        HubMenuMain.serverhud.addPlayer(player);
    }

    public void getServerPopulation(Player p){
        if (HubMenuMain.instance.serverPopulation.size() == 0) {
            BungeeListener.sendPluginMessage("GetServers", p, new String[]{});

            new BukkitRunnable() {
                @Override
                public void run() {
                    Player yahoo = p;
                    for (String s : HubMenuMain.lstServeurs) {
                        BungeeListener.sendPluginMessage("PlayerCount", yahoo, new String[]{s});
                    }
                }
            }.runTaskTimer(HubMenuMain.instance, 0L, 40L);
        }
    }

    @EventHandler
    public void OnInteract(PlayerInteractEvent event) {

        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        ItemStack itemHand = event.getItem();
        Player player = event.getPlayer();
        if (itemHand != null && itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName()
                && itemHand.getItemMeta().getDisplayName().equalsIgnoreCase(nomBoussole)) {
            getServerPopulation(player);
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
