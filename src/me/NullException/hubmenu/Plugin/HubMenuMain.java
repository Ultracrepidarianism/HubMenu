package me.NullException.hubmenu.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;



public class HubMenuMain extends JavaPlugin {
	
public static HubMenuMain instance;
public CustomMenu customMenu;
public static ServerItem serverItem;
public static BossBar serverhud;
	public void onEnable()
	{
		if (!getDataFolder().exists())
            getDataFolder().mkdir();
		saveDefaultConfig();
		try
		{
			serverhud.getTitle();
		}
		catch(NullPointerException npe)
		{
			System.out.println("bossbar doesn't exist");
			InstantiateBossBar();
		}
		serverItem = new ServerItem();
		instance = this;
		this.RegisterChannels();
		customMenu = new CustomMenu();
		customMenu.Reload();
		this.getCommand("editmenu").setExecutor(new Commands());
		this.getCommand("setdisplayname").setExecutor(new Commands());
		this.getCommand("setlore").setExecutor(new Commands());
		this.getCommand("setspawn").setExecutor(new Commands());
		this.getCommand("spawn").setExecutor(new Commands());
		this.getCommand("setenchant").setExecutor(new Commands());
		this.getCommand("setitemflag").setExecutor(new Commands());
		this.getCommand("addserveritem").setExecutor(new Commands());
		this.getCommand("fly").setExecutor(new Commands());
		this.getCommand("hudtoggle").setExecutor(new Commands());
		this.getCommand("hudsettitle").setExecutor(new Commands());
		this.getCommand("hudsetcolor").setExecutor(new Commands());
		this.getCommand("hudsetstyle").setExecutor(new Commands());
		this.getServer().getPluginManager().registerEvents(new PluginListener(), this);
	}
	public void OnDisable()
	{
		serverhud.removeAll();
	}
	public void RegisterChannels()
	{
		HubMenuMain.instance.getServer().getMessenger().registerOutgoingPluginChannel(HubMenuMain.instance, "BungeeCord");
		HubMenuMain.instance.getServer().getMessenger().registerIncomingPluginChannel(HubMenuMain.instance, "BungeeCord", serverItem );
		HubMenuMain.instance.getServer().getMessenger().registerIncomingPluginChannel(HubMenuMain.instance, "BungeeCord", new BungeeListener() );
	}
	
	public void InstantiateBossBar()
	{
		try {
			serverhud = Bukkit.createBossBar(getConfig().getString("bossbar.title").replaceAll("&", "§"),BarColor.valueOf(getConfig().getString("bossbar.color").toUpperCase()), BarStyle.valueOf(getConfig().getString("bossbar.style").toUpperCase()));
		}
		catch(Exception e)
		{
			Bukkit.getLogger().info(ChatColor.DARK_RED +"It seems like the bossbar has been set up incorrectly, Please check the plugin's config");
			e.printStackTrace();
		}
	}
}
