package com.eyeofender.serversigns.ping;

import java.net.InetSocketAddress;

import org.bukkit.ChatColor;

public class ServerInfo {
    private final String name;
    private final String displayName;
    private final InetSocketAddress address;

    private boolean online;
    private int onlinePlayers;
    private int maxPlayers;
    private String description;

    private String firstLine;
    private String secondLine;
    private String thirdLine;
    private String fourthLine;

    public ServerInfo(String name, String displayName, InetSocketAddress address) {
        this.name = name;
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.address = address;

        this.firstLine = getDisplayName();
        this.secondLine = "";
        this.thirdLine = "";
        this.fourthLine = "";

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
            generateLines();
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

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void generateLines() {
        if (online) {
            if (onlinePlayers == 1) {
                fourthLine = ChatColor.GREEN + "" + onlinePlayers + " Player";
            } else if (onlinePlayers == maxPlayers) {
                fourthLine = ChatColor.DARK_RED + "Full";
            } else {
                String colour = onlinePlayers >= maxPlayers - 2 ? ChatColor.YELLOW.toString() : ChatColor.GREEN.toString();
                fourthLine = colour + onlinePlayers + " Players";
            }

            thirdLine = description;
        } else {
            thirdLine = ChatColor.DARK_RED + "Offline";
            fourthLine = "";
        }
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public String getThirdLine() {
        return thirdLine;
    }

    public String getFourthLine() {
        return fourthLine;
    }

}