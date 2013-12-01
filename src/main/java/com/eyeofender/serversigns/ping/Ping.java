package com.eyeofender.serversigns.ping;

import java.io.IOException;

import org.bukkit.Bukkit;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.manager.SignManager;

public class Ping {

    private ServerSigns plugin;
    private MCPing ping;
    private boolean pinging;

    public Ping(ServerSigns plugin) {
        this.plugin = plugin;
        this.ping = new MCPing16();

        ping.setTimeout(plugin.getConfigManager().getTimeout());
    }

    public void startPingTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                pingAll();
            }
        }, 20, plugin.getConfigManager().getInterval() * 20);
    }

    public void pingAll() {
        if (pinging) return;
        pinging = true;

        for (ServerInfo info : plugin.getConfigManager().getServers()) {
            ping(info);
        }

        SignManager.updateSigns();
        pinging = false;
    }

    public void ping(ServerInfo info) {
        StatusResponse responce;
        ping.setAddress(info.getAddress());

        try {
            responce = ping.fetchData();
        } catch (IOException e1) {
            info.setOnline(false);
            plugin.getLogger().warning("Failed to ping " + info.getName() + "!");
            return;
        }

        info.setDescription(responce.getDescription());
        info.setOnlinePlayers(responce.getPlayers().getOnline());
        info.setMaxPlayers(responce.getPlayers().getMax());
        info.generateLines();
    }

}
