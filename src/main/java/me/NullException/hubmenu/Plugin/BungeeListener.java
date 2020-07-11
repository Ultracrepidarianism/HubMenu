package me.NullException.hubmenu.Plugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;

public class BungeeListener implements PluginMessageListener {


    @Override
    public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        System.out.println(channel);
        if(!channel.equals("BungeeCord"))
            return;
        System.out.println("Receiving PMC");
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String sub = in.readUTF();
        if(sub.equals("PlayerCount")) {
            String server = in.readUTF();
            Integer playercount = in.readInt();
            HubMenuMain.instance.serverPopulation.put(server,playercount);
        }
        if (sub.equals("GetServers"))
            HubMenuMain.instance.lstServeurs = Arrays.asList(in.readUTF().split(", "));
    }

    public synchronized void sendPluginMessage(String sub,Player ply,String args[])
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

}
