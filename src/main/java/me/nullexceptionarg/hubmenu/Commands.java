package me.nullexceptionarg.hubmenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Commands implements TabExecutor {
    FileConfiguration cfg = HubMenuMain.instance.getConfig();

	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

	    String lbl = command.getLabel();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (lbl.equalsIgnoreCase("editmenu")) {
                ServerItem serverItem = HubMenuMain.instance.serverItem;
                CustomMenu customMenu = HubMenuMain.instance.customMenu;
                if (player.hasPermission("hubmenu.editmenu.use")) {
                    if (args.length == 0) {
                        ItemStack[] actualMenuContent = customMenu.getInventory().getContents();

                        Inventory editmenu = Bukkit.createInventory(null, customMenu.getInventory().getSize(),
                                "editmenu");
                        Set<ItemStack> listItem = serverItem.GetItems().keySet();
                        for (ItemStack item : listItem)
                            player.getInventory().addItem(item);
                        editmenu.setContents(actualMenuContent);
                        player.openInventory(editmenu);
                        return true;
                    } else if (args.length == 1) {
                        List<Integer> tableSize = new ArrayList<>(Arrays.asList(9, 18, 27, 36, 45, 54));
                        if (!tableSize.contains(Integer.parseInt(args[0]))) {
                            player.sendMessage(ChatColor.RED
                                    + "Please use one of the following numbers as size : 9, 18, 27, 36, 45, 54");
                            return true;
                        }
                        Inventory editmenu = Bukkit.createInventory(null, Integer.parseInt(args[0]), "editmenu");
                        Set<ItemStack> listItem = serverItem.GetItems().keySet();
                        for (ItemStack item : listItem)
                            editmenu.addItem(item);
                        player.openInventory(editmenu);
                        return true;
                    }
                }
            }
            if (lbl.equalsIgnoreCase("setspawn")) {
                if (args.length == 0) {
                    cfg.set("spawnlocation", player.getLocation());
                    HubMenuMain.instance.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Spawn successfully set!");
                    return true;
                }
            } else if (lbl.equalsIgnoreCase("spawn")) {
                if (cfg.get("spawnlocation") instanceof Location) {
                    Location spawn = (Location) cfg.get("spawnlocation");
                    player.teleport(spawn);
                } else {
                    player.sendMessage(ChatColor.RED + "Please set the spawn first!");
                }
                return true;
            } else if (lbl.equalsIgnoreCase("Fly")) {
                if (player.hasPermission("hubmenu.fly")) {
                    player.setAllowFlight(!player.getAllowFlight());
                    player.setFlying(player.getAllowFlight());
                    String message = player.getAllowFlight() ? ChatColor.GREEN + "Fly enabled!"
                            : ChatColor.RED + "Fly disabled!";
                    player.sendMessage(message);
                    return true;
                }
            }
            // Server Item Perms
            if (player.hasPermission("hubmenu.serveritem.modify")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() == Material.AIR) player.sendMessage("You cannot modify air.");
                ItemMeta itemM = item.getItemMeta();
                if (lbl.equalsIgnoreCase("setdisplayname")) {

                    if (args.length >= 1) {
                        String displayname = "";
                        for (String addSpace : args) {
                            if (addSpace.equalsIgnoreCase(args[0]))
                                displayname = addSpace;
                            else
                                displayname += (" " + addSpace);
                        }
                        itemM.setDisplayName(CommonUtils.colorize(displayname));
                        item.setItemMeta(itemM);
                        player.getInventory().setItemInMainHand(item);
                        return true;
                    }
                } else if (lbl.equalsIgnoreCase("setlore")) {
                    if (args.length >= 2) {
                        try {
                            Integer testInt = Integer.parseInt(args[0]);
                            if(testInt < 1){
                                player.sendMessage("Please use line 1 and above.");
                                return true;
                            }


                        } catch (Exception e) {
                            return false;
                        }

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
                        listLore.set((Integer.parseInt(args[0]) - 1), CommonUtils.colorize(lore));
                        itemM.setLore(listLore);
                        item.setItemMeta(itemM);
                        player.getInventory().setItemInMainHand(item);
                        return true;
                    }
                } else if (lbl.equalsIgnoreCase("setenchant")) {
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
                } else if (lbl.equalsIgnoreCase("setitemflag")) {
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
                } else if (lbl.equalsIgnoreCase("addserveritem")) {
                    ServerItem serverItem = HubMenuMain.instance.serverItem;
                    if (args.length == 1) {
                        serverItem.addItem(player.getInventory().getItemInMainHand(), args[0], player);
                        return true;
                    }
                }
            }

            // HUD perms
            if (player.hasPermission("hubmenu.hud.modify")) {
                if (lbl.equalsIgnoreCase("togglehud")) {
                    if (args.length == 0) {
                        HubMenuMain.instance.serverhud.setVisible(!HubMenuMain.instance.serverhud.isVisible());
                        return true;
                    }
                } else if (lbl.equalsIgnoreCase("sethudstyle")) {
                    if (args.length == 1) {
                        try {
                            HubMenuMain.instance.serverhud.setStyle(BarStyle.valueOf(args[0].toUpperCase()));
                            cfg.set("bossbar.style", HubMenuMain.instance.serverhud.getStyle().name());
                            HubMenuMain.instance.saveConfig();
                            return true;
                        } catch (IllegalArgumentException e) {
                            String line = ChatColor.RED + "Please use one of the following: \n";
                            for (BarStyle barstyle : BarStyle.values()) {
                                if (barstyle.equals(BarStyle.values()[BarStyle.values().length - 1])) {
                                    line += barstyle.name();
                                } else {
                                    line += barstyle.name() + ",";
                                }
                            }
                            player.sendMessage(line);
                        }
                    }
                } else if (lbl.equalsIgnoreCase("sethudtitle")) {
                    if (args.length >= 1) {
                        String title = args[0];
                        for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
                            title += " " + arg;
                        }
                        try {
                            HubMenuMain.instance.serverhud.setTitle(CommonUtils.colorize(title));
                            cfg.set("bossbar.title", title);
                            HubMenuMain.instance.saveConfig();
                            return true;
                        } catch (Exception e) {
                            player.sendMessage("Please send me the StackTrace and i'm gonna try to fix your issue");
                            e.printStackTrace();
                        }
                    }
                } else if (lbl.equalsIgnoreCase("sethudcolor")) {
                    if (args.length == 1) {
                        try {
                            HubMenuMain.instance.serverhud.setColor(BarColor.valueOf(args[0].toUpperCase()));
                            cfg.set("bossbar.color", HubMenuMain.instance.serverhud.getColor().name());
                            HubMenuMain.instance.saveConfig();
                            return true;
                        } catch (IllegalArgumentException e) {
                            String line = ChatColor.RED + "Please use one of the following: \n";
                            for (BarColor barstyle : BarColor.values()) {
                                if (barstyle.equals(BarColor.values()[BarColor.values().length - 1])) {
                                    line += barstyle.name();
                                } else {
                                    line += barstyle.name() + ",";
                                }
                            }
                            player.sendMessage(line);
                        }
                    }
                }
            }

        } else {
            HubMenuMain.instance.getLogger().info(ChatColor.RED + "You must be a player to use this command");
            return true;
        }
        return false;
    }

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] args) {
	    List<String> tab = new ArrayList<>();
		if(lbl.equalsIgnoreCase("sethudcolor")){
		    if(args.length == 1){
		        for(BarColor color : BarColor.values()){
		            tab.add(color.name());
                }
		        return tab;
            }
        }else if(lbl.equalsIgnoreCase("sethudstyle")){
		    if(args.length == 1){
		        for(BarStyle style : BarStyle.values()){
		            tab.add(style.name());
                }
		        return tab;
            }
        }else if(lbl.equalsIgnoreCase("editmenu")){
            if(args.length == 1){
                for(int i = 9 ; i < 54; i+= 9){
                    if(i == 9)
                        tab.add("09");
                    else
                        tab.add("" + i);
                }
                return tab;
            }
        }else if(lbl.equalsIgnoreCase("setlore")){
		    if(args.length == 1){
		        tab.add("LineNumber");
		        return tab;
            }
        }else if(lbl.equalsIgnoreCase("setenchant")){
		    if(args.length == 1){
		        for(Enchantment en : Enchantment.values()){
		            tab.add(en.getName());
                }
		        return tab;
            }else if(args.length == 2){
		        tab= new ArrayList<>();
		        Enchantment en = Enchantment.getByName(args[0]);
		        if(en != null){
		            for (int i = 1; i < en.getMaxLevel(); i++){
		                tab.add("" +i);
                    }
		            return tab;
                }
            }
        }else if(lbl.equalsIgnoreCase("setitemflag")){
		    if(args.length == 1){
		        for(ItemFlag itf : ItemFlag.values()){
		            tab.add(itf.name());
                }
		        return tab;
            }
        }else if(lbl.equalsIgnoreCase("addserveritem")){
		    if(args.length == 1){
		        return HubMenuMain.instance.lstServeurs;
            }
        }

		return null;
	}
}
