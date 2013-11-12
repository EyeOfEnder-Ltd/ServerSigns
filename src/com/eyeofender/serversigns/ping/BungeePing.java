package com.eyeofender.serversigns.ping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.TeleportSign;

public class BungeePing implements PluginMessageListener {

    private ServerSigns plugin;

    public BungeePing(ServerSigns plugin) {
        this.plugin = plugin;
    }

    public void pingAll() {
        for (ServerInfo info : plugin.getConfigData().getServers()) {
            ping(info);
        }

        for (TeleportSign sign : plugin.getSigns()) {
            sign.updateSign();
        }
    }

    public void ping(ServerInfo info) {
        try {
            fetchOnlinePlayers(info.getName());
            info.setOnline(true);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to ping " + info.getName() + "!");
            info.setOnline(false);
            info.setOnlinePlayers(0);
        }
    }

    private void fetchOnlinePlayers(String name) throws IOException {
        if (Bukkit.getOnlinePlayers().length <= 0) return;
        Player p = Bukkit.getOnlinePlayers()[0];

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        out.writeUTF("PlayerCount");
        out.writeUTF(name);
        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String subchannel;
        try {
            subchannel = in.readUTF();
            if (subchannel.equals("PlayerCount")) {
                plugin.getConfigData().getServer(in.readUTF()).setOnlinePlayers(in.readInt());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to interperet message!");
        }
    }

}
