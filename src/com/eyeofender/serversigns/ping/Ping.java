package com.eyeofender.serversigns.ping;

import org.bukkit.Bukkit;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.TeleportSign;

public class Ping implements Runnable {
    private final MCPing mcping;
    private ServerSigns plugin;

    public Ping(ServerSigns plugin) {
        this.plugin = plugin;
        this.mcping = new MCPing();
    }

    public void run() {
        for (ServerInfo info : plugin.getConfigData().getServers()) {
            mcping.setAddress(info.getAddress());
            mcping.setTimeout(plugin.getConfigData().getTimeout());
            if (mcping.fetchData(plugin)) {
                info.setOnline(true);
                info.setMotd(this.mcping.getMotd().split("(?<=\\G.{15})"));
                info.setPlayersOnline(mcping.getPlayersOnline());
                info.setMaxPlayers(mcping.getMaxPlayers());
            } else {
                info.setOnline(false);
                info.setMotd(null);
            }
        }

        for (TeleportSign sign : plugin.getSigns()) {
            sign.updateSign();
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, plugin.getConfigData().getInterval() * 20);
    }

}