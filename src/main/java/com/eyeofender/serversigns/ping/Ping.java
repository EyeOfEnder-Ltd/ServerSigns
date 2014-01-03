package com.eyeofender.serversigns.ping;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.manager.SignManager;
import com.eyeofender.serversigns.ping.minecraft.MCPing;
import com.eyeofender.serversigns.ping.minecraft.MCPing16;

public class Ping {

    private ServerSigns plugin;
    private MCPing ping;
    private Queue<ServerInfo> queue;

    public Ping(ServerSigns plugin) {
        this.plugin = plugin;
        this.ping = new MCPing16();
        this.queue = new LinkedList<ServerInfo>();

        ping.setTimeout(plugin.getConfigManager().getTimeout());
    }

    public void startPingTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (queue.isEmpty()) {
                    queue.addAll(plugin.getConfigManager().getServers());
                }

                ping(queue.poll());
            }
        }, 10, 10);
    }

    public void ping(ServerInfo info) {
        if (info == null) return;
        StatusResponse response;
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

        SignManager.updateSigns(info);
    }

}
