package com.eyeofender.serversigns.manager;

import java.net.InetSocketAddress;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.ping.ServerInfo;
import com.google.common.collect.Lists;

public class ConfigManager {

    private final ServerSigns plugin;

    private int interval;
    private int timeout;
    private int cooldown;
    private List<ServerInfo> servers;

    public ConfigManager(ServerSigns plugin) {
        this.plugin = plugin;
    }

    public void saveDefault() {
        plugin.saveDefaultConfig();
    }

    public void load() {
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
        this.interval = config.getInt("interval", 5);
        this.timeout = config.getInt("timeout", 5000);
        this.cooldown = config.getInt("cooldown", 2000);
        this.servers = loadServers(config);
    }

    private List<ServerInfo> loadServers(FileConfiguration config) {
        List<ServerInfo> servers = Lists.newArrayList();

        for (String name : config.getConfigurationSection("servers").getKeys(false)) {
            String path = "servers." + name + ".";

            String displayName = config.getString(path + "displayname", name);
            String hostname = config.getString(path + "hostname", "localhost");
            int port = config.getInt(path + "port", 25565);

            servers.add(new ServerInfo(name, displayName, new InetSocketAddress(hostname, port)));
        }

        return servers;
    }

    public int getInterval() {
        return interval;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<ServerInfo> getServers() {
        return servers;
    }

    public ServerInfo getServer(String name) {
        for (ServerInfo info : servers) {
            if (info.getName().equalsIgnoreCase(name)) return info;
        }
        return null;
    }

}
