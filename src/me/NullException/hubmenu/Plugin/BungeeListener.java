package me.NullException.hubmenu.Plugin;


import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements PluginMessageListener {

	public static String ip = "";
	public static int port = 0;
	public static int players = 0;
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) 
	{
		receivePluginMessage(channel, message);
	}
	
	public static void sendPluginMessage(String sub,Player ply,String args[])
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(sub);
		for(String arg : args) 
		{
			out.writeUTF(arg);
		}
		ply.sendPluginMessage(HubMenuMain.instance, "BungeeCord", out.toByteArray());
	}

	public static void receivePluginMessage(String channel, byte[] bytes) {
		if(!channel.equals("BungeeCord"))
			return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String sub = in.readUTF();
		if(sub.equals("IP"))
		{
			ip = in.readUTF();
			port = in.readInt();
		}
		if(sub.contentEquals("PlayerCount"))
		{
			in.readUTF();
			players = in.readInt(); 
		}
	}
}
