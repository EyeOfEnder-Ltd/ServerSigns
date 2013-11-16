package com.eyeofender.serversigns.ping;

import java.net.InetSocketAddress;

import org.bukkit.ChatColor;

public class ServerInfo {
    private static final String OFFLINE = ChatColor.DARK_RED + "Offline";

    private final String name;
    private final String displayName;
    private final InetSocketAddress address;

    private boolean online;
    private int onlinePlayers;
    private int maxPlayers;
    private String description;

    public ServerInfo(String name, String displayName, InetSocketAddress address) {
        this.name = name;
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.address = address;

        setOnline(false);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;

        if (!online) {
            setOnlinePlayers(0);
            setMaxPlayers(0);
            setDescription(OFFLINE);
        }
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getOffline() {
        return OFFLINE;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

}