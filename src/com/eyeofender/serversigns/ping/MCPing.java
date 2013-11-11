package com.eyeofender.serversigns.ping;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public final class MCPing {
    private InetSocketAddress address;
    private int timeout = 2500;

    private int pingVersion = -1;
    private int protocolVersion = -1;
    private String gameVersion;
    private String motd;
    private int playersOnline = -1;
    private int maxPlayers = -1;

    public boolean fetchData() {
        try {
            Socket socket = new Socket();

            socket.setSoTimeout(this.timeout);

            socket.connect(this.address, getTimeout());

            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-16BE"));

            dataOutputStream.write(new byte[] { -2, 1, -6 });

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

            if (packetId != 255) {
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
                String[] data = string.split("");

                this.pingVersion = Integer.parseInt(data[0].substring(1));
                this.protocolVersion = Integer.parseInt(data[1]);
                this.gameVersion = data[2];
                this.motd = data[3];
                this.playersOnline = Integer.parseInt(data[4]);
                this.maxPlayers = Integer.parseInt(data[5]);
            } else {
                String[] data = string.split("§");

                this.motd = data[0];
                this.playersOnline = Integer.parseInt(data[1]);
                this.maxPlayers = Integer.parseInt(data[2]);
            }
            dataOutputStream.close();
            outputStream.close();

            inputStreamReader.close();
            inputStream.close();
            socket.close();
        } catch (Exception exception) {
            if (!(exception instanceof ConnectException)) Bukkit.getLogger().log(Level.SEVERE, "Error fetching data from server " + this.address.toString(), exception);
            return false;
        }
        return true;
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
