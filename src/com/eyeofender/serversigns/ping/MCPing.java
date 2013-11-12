package com.eyeofender.serversigns.ping;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import org.bukkit.plugin.Plugin;

public final class MCPing {
    private InetSocketAddress address;
    private int timeout = 2500;

    private int pingVersion = -1;
    private int protocolVersion = -1;
    private String gameVersion;
    private String motd;
    private int playersOnline = -1;
    private int maxPlayers = -1;

    public boolean fetchData(Plugin plugin) {
        try {
            Socket socket = new Socket();
            OutputStream outputStream;
            DataOutputStream dataOutputStream;
            InputStream inputStream;
            InputStreamReader inputStreamReader;

            socket.setSoTimeout(timeout);

            socket.connect(address, getTimeout());

            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);

            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-16BE"));

            dataOutputStream.write(new byte[] { (byte) 0xFE, (byte) 0x01, (byte) 0xFA });

            dataOutputStream.write("MC|PingHost".getBytes("UTF-16BE"));
            dataOutputStream.writeShort(this.address.getHostName().length() * 2 + 7);
            dataOutputStream.write(74);
            dataOutputStream.writeShort(this.address.getHostName().length());
            dataOutputStream.write(this.address.getHostName().getBytes("UTF-16BE"));
            dataOutputStream.writeInt(this.address.getPort());

            int packetId = inputStream.read();

            if (packetId == -1) {
                throw new IOException("Premature end of stream.");
            }

            if (packetId != 0xFF) {
                throw new IOException("Invalid packet ID (" + packetId + ").");
            }

            int length = inputStreamReader.read();

            if (length == -1) {
                throw new IOException("Premature end of stream.");
            }

            if (length == 0) {
                throw new IOException("Invalid string length.");
            }

            char[] chars = new char[length];

            if (inputStreamReader.read(chars, 0, length) != length) {
                throw new IOException("Premature end of stream.");
            }

            String string = new String(chars);

            if (string.startsWith("§")) {
                String[] data = string.split("\0");

                pingVersion = Integer.parseInt(data[0].substring(1));
                protocolVersion = Integer.parseInt(data[1]);
                gameVersion = data[2];
                motd = data[3];
                playersOnline = Integer.parseInt(data[4]);
                maxPlayers = Integer.parseInt(data[5]);
            } else {
                String[] data = string.split("§");

                motd = data[0];
                playersOnline = Integer.parseInt(data[1]);
                maxPlayers = Integer.parseInt(data[2]);
            }

            dataOutputStream.close();
            outputStream.close();

            inputStreamReader.close();
            inputStream.close();

            socket.close();

            return true;
        } catch (Exception exception) {
            plugin.getLogger().warning("Could not ping " + address + "!");
            exception.getMessage();
            return false;
        }
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public int getPingVersion() {
        return this.pingVersion;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getMotd() {
        return this.motd;
    }

    public int getPlayersOnline() {
        return this.playersOnline;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
