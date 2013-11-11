package com.eyeofender.serversigns.ping;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;

import com.eyeofender.serversigns.TeleportSign;
import com.eyeofender.serversigns.ServerSigns;
import com.google.common.collect.Lists;

public class Ping implements Runnable {
    private final MCPing mcping;
    private ServerSigns plugin;
    private int signsPerTick;

    public Ping(ServerSigns plugin) {
        this.plugin = plugin;
        this.mcping = new MCPing();
        this.signsPerTick = plugin.getConfigData().getSignsPerTick();
    }

    public void run() {
        for (ServerInfo info : this.plugin.getConfigData().getServers()) {
            this.mcping.setAddress(info.getAddress());
            this.mcping.setTimeout(this.plugin.getConfigData().getTimeout());
            if (this.mcping.fetchData()) {
                info.setOnline(true);
                info.setMotd(this.mcping.getMotd().split("(?<=\\G.{15})"));
                info.setPlayersOnline(this.mcping.getPlayersOnline());
                info.setMaxPlayers(this.mcping.getMaxPlayers());
            } else {
                info.setOnline(false);
                info.setMotd(null);
            }
            List<TeleportSign> tempList = Lists.newArrayList();
            Iterator<TeleportSign> iterator = this.plugin.getSigns().iterator();
            while (iterator.hasNext()) {
                TeleportSign ts = (TeleportSign) iterator.next();
                if (ts.getServer().equals(info.getName())) {
                    tempList.add(ts);
                }
            }
            int size = tempList.size();
            int offset = 0;
            while (size > this.signsPerTick) {
                this.plugin.updateSigns(tempList.subList(offset, offset + this.signsPerTick));
                size -= this.signsPerTick;
                offset += this.signsPerTick;
            }
            this.plugin.updateSigns(tempList.subList(offset, offset + size));
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, this, this.plugin.getConfigData().getInterval() * 20);
    }

}