package me.NullException.hubmenu.Plugin;


import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Arrays;

public class BungeeListener implements PluginMessageListener {

	public static String ip = "";
	public static int port = 0;
	public static int players = 0;

	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		receivePluginMessage(channel, message);
	}

	public synchronized static void sendPluginMessage(String sub,Player ply,String args[])
	{
		System.out.println("Sending PMC");
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(sub);
		for(String arg : args) 
		{
			out.writeUTF(arg);
		}
		ply.sendPluginMessage(HubMenuMain.instance, "BungeeCord", out.toByteArray());
	}

	public synchronized  static void receivePluginMessage(String channel, byte[] bytes) {
		if(!channel.equals("BungeeCord"))
			return;
		System.out.println("RECEIVING PMC");
		
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String sub = in.readUTF();
		if(sub.equals("IP"))
		{
			ip = in.readUTF();
			port = in.readInt();
		}
		if(sub.equals("PlayerCount"))
		{
			String server = in.readUTF();
			Integer playercount = in.readInt();
			HubMenuMain.instance.serverPopulation.put(server,playercount);
		}
		if (sub.equals("GetServers")) {
			HubMenuMain.instance.lstServeurs = Arrays.asList(in.readUTF().split(", "));
		}
	}
}
