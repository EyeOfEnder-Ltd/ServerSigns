package com.eyeofender.serversigns.ping;

import java.io.IOException;

import org.bukkit.Bukkit;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.manager.SignManager;
import com.eyeofender.serversigns.ping.minecraft.MCPing;
import com.eyeofender.serversigns.ping.minecraft.MCPing16;

public class Ping {

    private ServerSigns plugin;
    private boolean pinging;

    public Ping(ServerSigns plugin) {
        this.plugin = plugin;
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
            if (SignManager.hasSign(info)) ping(info);
        }

        SignManager.updateSigns();
        pinging = false;
    }

    public void ping(ServerInfo info) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new PingRunnable(info));
    }

    private class PingRunnable implements Runnable {

        private ServerInfo info;

        public PingRunnable(ServerInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            StatusResponse response;
            MCPing ping = new MCPing16();
            ping.setTimeout(plugin.getConfigManager().getTimeout());

            ping.setAddress(info.getAddress());

            try {
                response = ping.fetchData();
            } catch (IOException ex) {
                info.setOnline(false);
                plugin.getLogger().warning("Failed to ping " + info.getName() + "!  " + ex.getMessage());
                return;
            }

            info.setOnline(true);
            info.setDescription(response.getDescription());
            info.setOnlinePlayers(response.getPlayers().getOnline());
            info.setMaxPlayers(response.getPlayers().getMax());
            info.generateLines();
        }

    }

}
