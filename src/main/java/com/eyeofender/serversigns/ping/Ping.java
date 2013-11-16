package com.eyeofender.serversigns.ping;

import java.io.IOException;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.manager.SignManager;
import com.eyeofender.serversigns.ping.MCPing17.Players;
import com.eyeofender.serversigns.ping.MCPing17.StatusResponse;

public class Ping {

    private ServerSigns plugin;
    private MCPing17 ping;

    public Ping(ServerSigns plugin) {
        this.plugin = plugin;
        this.ping = new MCPing17();

        ping.setTimeout(plugin.getConfigManager().getTimeout());
    }

    public void pingAll() {
        for (ServerInfo info : plugin.getConfigManager().getServers()) {
            ping(info);
        }

        SignManager.updateSigns();
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

        Players players = responce.getPlayers();

        info.setDescription(responce.getDescription());
        info.setOnlinePlayers(players.getOnline());
        info.setMaxPlayers(players.getMax());
    }

}
