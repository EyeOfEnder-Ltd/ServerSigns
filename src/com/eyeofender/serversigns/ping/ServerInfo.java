package com.eyeofender.serversigns.ping;

import java.beans.ConstructorProperties;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class ServerInfo {
    private final String name;
    private int playersOnline;
    private int maxPlayers;
    private String[] motd;
    private boolean online;
    private final InetSocketAddress address;
    private final String displayname;

    @ConstructorProperties({ "name", "address", "displayname" })
    public ServerInfo(String name, InetSocketAddress address, String displayname) {
        this.playersOnline = 0;
        this.maxPlayers = 0;

        this.online = false;
        this.name = name;
        this.address = address;
        this.displayname = displayname;
    }

    public String getName() {
        return this.name;
    }

    public int getPlayersOnline() {
        return this.playersOnline;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String[] getMotd() {
        return this.motd;
    }

    public boolean isOnline() {
        return this.online;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public String getDisplayname() {
        return this.displayname;
    }

    public void setPlayersOnline(int playersOnline) {
        this.playersOnline = playersOnline;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMotd(String[] motd) {
        this.motd = motd;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ServerInfo)) return false;
        ServerInfo other = (ServerInfo) o;
        if (!other.canEqual(this)) return false;
        Object this$name = getName();
        Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        if (getPlayersOnline() != other.getPlayersOnline()) return false;
        if (getMaxPlayers() != other.getMaxPlayers()) return false;
        if (!Arrays.deepEquals(getMotd(), other.getMotd())) return false;
        if (isOnline() != other.isOnline()) return false;
        Object this$address = getAddress();
        Object other$address = other.getAddress();
        if (this$address == null ? other$address != null : !this$address.equals(other$address)) return false;
        Object this$displayname = getDisplayname();
        Object other$displayname = other.getDisplayname();
        return this$displayname == null ? other$displayname == null : this$displayname.equals(other$displayname);
    }

    public boolean canEqual(Object other) {
        return other instanceof ServerInfo;
    }

    public int hashCode() {
        int result = 1;
        Object $name = getName();
        result = result * 31 + ($name == null ? 0 : $name.hashCode());
        result = result * 31 + getPlayersOnline();
        result = result * 31 + getMaxPlayers();
        result = result * 31 + Arrays.deepHashCode(getMotd());
        result = result * 31 + (isOnline() ? 1231 : 1237);
        Object $address = getAddress();
        result = result * 31 + ($address == null ? 0 : $address.hashCode());
        Object $displayname = getDisplayname();
        result = result * 31 + ($displayname == null ? 0 : $displayname.hashCode());
        return result;
    }

    public String toString() {
        return "ServerInfo(name=" + getName() + ", playersOnline=" + getPlayersOnline() + ", maxPlayers=" + getMaxPlayers() + ", motd=" + Arrays.deepToString(getMotd()) + ", online=" + isOnline()
                + ", address=" + getAddress() + ", displayname=" + getDisplayname() + ")";
    }

}