package me.NullException.hubmenu.Plugin;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketMenu 
{
	private Socket socket = new Socket();
	private String[]data = new String[666];
	
	public SocketMenu(String host,int port)
	{
		try {
			socket.connect(new InetSocketAddress(host,port));
			OutputStream out = socket.getOutputStream();
			InputStream  in = socket.getInputStream();
			out.write(0xFE);
			int b;
			StringBuffer str = new StringBuffer();
			while((b = in.read()) != -1)
			{
				if(b > 16 && b != 255 && b != 23 && b != 24)
					str.append((char)b);
			}
			data = str.toString().split("§");
			data[1] = data[1].substring(1);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	public int getMaxPlayers()
	{
		return Integer.parseInt(data[2]);
	}
}
