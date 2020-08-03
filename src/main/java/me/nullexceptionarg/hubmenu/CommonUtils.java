package me.nullexceptionarg.hubmenu;


import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommonUtils {


    private CommonUtils(){}

    public static String colorize(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        Pattern hexPattern = Pattern.compile("(#([a-fA-F0-9]{6}))");
        Matcher hexMatch = hexPattern.matcher(input);
        if(hexMatch.find()){
            StringBuffer buffer = new StringBuffer();
            do{
                    hexMatch.appendReplacement(buffer, "" + ChatColor.of(hexMatch.group(1)));
            }while(hexMatch.find());

            hexMatch.appendTail(buffer);
            input = buffer.toString();
        }

        return input;
    }
}
