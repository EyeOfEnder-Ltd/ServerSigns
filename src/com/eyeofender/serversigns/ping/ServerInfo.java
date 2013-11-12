package com.eyeofender.serversigns.ping;

import java.beans.ConstructorProperties;

public class ServerInfo {
    private final String name;
    private int onlinePlayers;
    private boolean online;
    private final String displayname;

    @ConstructorProperties({ "name", "displayname" })
    public ServerInfo(String name, String displayname) {
        this.onlinePlayers = 0;

        this.online = false;
        this.name = name;
        this.displayname = displayname;
    }

    public String getName() {
        return this.name;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public boolean isOnline() {
        return this.online;
    }

    public String getDisplayname() {
        return this.displayname;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
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
        if (getOnlinePlayers() != other.getOnlinePlayers()) return false;
        if (isOnline() != other.isOnline()) return false;
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
        result = result * 31 + getOnlinePlayers();
        result = result * 31 + (isOnline() ? 1231 : 1237);
        Object $displayname = getDisplayname();
        result = result * 31 + ($displayname == null ? 0 : $displayname.hashCode());
        return result;
    }

    public String toString() {
        return "ServerInfo(name=" + getName() + ", playersOnline=" + getOnlinePlayers() + ", online=" + isOnline() + ", displayname=" + getDisplayname() + ")";
    }

}