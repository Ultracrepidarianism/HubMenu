package me.nullexceptionarg.hubmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HubMenuMain extends JavaPlugin {
	
public static HubMenuMain instance;
public CustomMenu customMenu;
public List<String> lstServeurs;
public ServerItem serverItem;
public static BungeeListener bl;
public BossBar serverhud;
public Map<String, Integer> serverPopulation;

	public void onEnable()
	{
		if (!getDataFolder().exists())
            getDataFolder().mkdir();
		saveDefaultConfig();
		serverhud = null;
		InstantiateBossBar();
		serverPopulation = new HashMap<>();
		serverItem = new ServerItem();
		lstServeurs = new ArrayList<>();
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
		this.getCommand("togglehud").setExecutor(new Commands());
		this.getCommand("sethudtitle").setExecutor(new Commands());
		this.getCommand("sethudstyle").setExecutor(new Commands());
		this.getCommand("sethudcolor").setExecutor(new Commands());
		this.getServer().getPluginManager().registerEvents(new PluginListener(), this);
	}
	public void onDisable()
	{
		serverhud.removeAll();
	}
	public void RegisterChannels()
	{
		HubMenuMain.instance.getServer().getMessenger().registerOutgoingPluginChannel(HubMenuMain.instance, "BungeeCord");
		HubMenuMain.instance.getServer().getMessenger().registerIncomingPluginChannel(HubMenuMain.instance, "BungeeCord", serverItem );
		HubMenuMain.instance.getServer().getMessenger().registerIncomingPluginChannel(HubMenuMain.instance, "BungeeCord", bl = new BungeeListener()  );
	}
	
	public void InstantiateBossBar()
	{
		try {
			serverhud = Bukkit.createBossBar(CommonUtils.colorize(getConfig().getString("bossbar.title")),BarColor.valueOf(getConfig().getString("bossbar.color").toUpperCase()), BarStyle.valueOf(getConfig().getString("bossbar.style").toUpperCase()));
		}
		catch(Exception e)
		{
			Bukkit.getLogger().info(ChatColor.DARK_RED +"It seems like the bossbar has been set up incorrectly, Please check the plugin's config");
			e.printStackTrace();
		}
	}
}
