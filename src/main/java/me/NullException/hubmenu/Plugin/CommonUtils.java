package me.NullException.hubmenu.Plugin;

import org.bukkit.ChatColor;

public final class CommonUtils {

    private CommonUtils(){}

    public static String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }


}
