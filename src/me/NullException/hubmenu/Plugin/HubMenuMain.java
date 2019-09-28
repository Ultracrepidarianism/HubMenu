package me.NullException.hubmenu.Plugin;

import org.bukkit.plugin.java.JavaPlugin;


public class HubMenuMain extends JavaPlugin {
	
public static HubMenuMain instance;
public static CustomMenu customMenu;
public static ServerItem serverItem;
	public void onEnable()
	{
		if (!getDataFolder().exists())
            getDataFolder().mkdir();
		saveDefaultConfig();
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
		this.getCommand("spawn").setExecutor(new Commands());
		this.getCommand("spawn").setExecutor(new Commands());
		this.getCommand("fly").setExecutor(new Commands());
		this.getServer().getPluginManager().registerEvents(new PluginListener(), this);
	}
	public void OnDisable()
	{
		saveConfig();
	}
	public void RegisterChannels()
	{
		HubMenuMain.instance.getServer().getMessenger().registerOutgoingPluginChannel(HubMenuMain.instance, "BungeeCord");
		HubMenuMain.instance.getServer().getMessenger().registerIncomingPluginChannel(HubMenuMain.instance, "BungeeCord", serverItem );
		HubMenuMain.instance.getServer().getMessenger().registerIncomingPluginChannel(HubMenuMain.instance, "BungeeCord", new BungeeListener() );
	}
}
