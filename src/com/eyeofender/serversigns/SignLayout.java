package com.eyeofender.serversigns;

import java.beans.ConstructorProperties;
import java.util.List;

import org.bukkit.ChatColor;

import com.eyeofender.serversigns.ping.ServerInfo;
import com.google.common.collect.Lists;

class SignLayout {
    private final String name;
    private final String online;
    private final String offline;
    private final List<String> lines;
    private final boolean teleport;
    private final String offlineInteger;

    public List<String> parseLayout(ServerInfo sinfo) {
        List<String> laa = Lists.newArrayList();
        int motdCount = 0;
        for (String line : this.lines) {
            line = line.replace("%displayname%", sinfo.getDisplayname());
            if (sinfo.isOnline()) {
                line = line.replace("%isonline%", this.online);
                line = line.replace("%numpl%", String.valueOf(sinfo.getPlayersOnline()));
                line = line.replace("%maxpl%", String.valueOf(sinfo.getMaxPlayers()));
                if (line.contains("%motd%")) if (motdCount < sinfo.getMotd().length) {
                    String motd = sinfo.getMotd()[motdCount];
                    if (motd != null) {
                        line = line.replace("%motd%", motd);
                    }
                    motdCount++;
                } else {
                    line = line.replace("%motd%", "");
                }
            } else {
                line = line.replace("%isonline%", this.offline);
                line = line.replace("%numpl%", this.offlineInteger);
                line = line.replace("%maxpl%", this.offlineInteger);
                line = line.replace("%motd%", "");
            }
            laa.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return laa;
    }

    @ConstructorProperties({ "name", "online", "offline", "lines", "teleport", "offlineInteger" })
    public SignLayout(String name, String online, String offline, List<String> lines, boolean teleport, String offlineInteger) {
        this.name = name;
        this.online = online;
        this.offline = offline;
        this.lines = lines;
        this.teleport = teleport;
        this.offlineInteger = offlineInteger;
    }

    public String getName() {
        return this.name;
    }

    public String getOnline() {
        return this.online;
    }

    public String getOffline() {
        return this.offline;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public boolean isTeleport() {
        return this.teleport;
    }

    public String getOfflineInteger() {
        return this.offlineInteger;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SignLayout)) return false;
        SignLayout other = (SignLayout) o;
        if (!other.canEqual(this)) return false;
        Object this$name = getName();
        Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        Object this$online = getOnline();
        Object other$online = other.getOnline();
        if (this$online == null ? other$online != null : !this$online.equals(other$online)) return false;
        Object this$offline = getOffline();
        Object other$offline = other.getOffline();
        if (this$offline == null ? other$offline != null : !this$offline.equals(other$offline)) return false;
        Object this$lines = getLines();
        Object other$lines = other.getLines();
        if (this$lines == null ? other$lines != null : !this$lines.equals(other$lines)) return false;
        if (isTeleport() != other.isTeleport()) return false;
        Object this$offlineInteger = getOfflineInteger();
        Object other$offlineInteger = other.getOfflineInteger();
        return this$offlineInteger == null ? other$offlineInteger == null : this$offlineInteger.equals(other$offlineInteger);
    }

    public boolean canEqual(Object other) {
        return other instanceof SignLayout;
    }

    public int hashCode() {
        int result = 1;
        Object $name = getName();
        result = result * 31 + ($name == null ? 0 : $name.hashCode());
        Object $online = getOnline();
        result = result * 31 + ($online == null ? 0 : $online.hashCode());
        Object $offline = getOffline();
        result = result * 31 + ($offline == null ? 0 : $offline.hashCode());
        Object $lines = getLines();
        result = result * 31 + ($lines == null ? 0 : $lines.hashCode());
        result = result * 31 + (isTeleport() ? 1231 : 1237);
        Object $offlineInteger = getOfflineInteger();
        result = result * 31 + ($offlineInteger == null ? 0 : $offlineInteger.hashCode());
        return result;
    }

    public String toString() {
        return "SignLayout(name=" + getName() + ", online=" + getOnline() + ", offline=" + getOffline() + ", lines=" + getLines() + ", teleport=" + isTeleport() + ", offlineInteger="
                + getOfflineInteger() + ")";
    }
}
