package com.eyeofender.serversigns;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.eyeofender.serversigns.ping.ServerInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConfigurationData {
    private ServerSigns plugin;
    private FileConfiguration config;
    private String offlineMessage;
    private List<ServerInfo> servers;
    private Map<String, SignLayout> signLayouts;
    private int interval;
    private int timeout;
    private boolean showOfflineMsg;
    private int cooldown;

    public ConfigurationData(ServerSigns plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public void loadConfig() {
        this.config = this.plugin.getConfig();
        this.offlineMessage = this.config.getString("offline-message");
        this.showOfflineMsg = this.config.getBoolean("show-offline-message");
        this.interval = this.config.getInt("interval");
        this.timeout = this.config.getInt("timeout");
        this.cooldown = this.config.getInt("cooldown");
        this.signLayouts = loadLayouts();
        this.servers = loadServers();
    }

    public void reloadConfig() {
        this.plugin.reloadConfig();
        loadConfig();
    }

    private Map<String, SignLayout> loadLayouts() {
        Map<String, SignLayout> layoutMap = Maps.newHashMap();
        ConfigurationSection layouts = this.config.getConfigurationSection("layouts");
        for (String layout : layouts.getKeys(false)) {
            ConfigurationSection cs = layouts.getConfigurationSection(layout);
            String online = cs.getString("online");
            String offline = cs.getString("offline");
            List<String> lines = cs.getStringList("layout");
            boolean teleport = cs.getBoolean("teleport");
            String offlineInteger = cs.getString("offline-int");
            SignLayout signLayout = new SignLayout(layout, online, offline, lines, teleport, offlineInteger);
            layoutMap.put(layout, signLayout);
        }
        return layoutMap;
    }

    private List<ServerInfo> loadServers() {
        List<ServerInfo> list = Lists.newArrayList();
        ConfigurationSection server = this.config.getConfigurationSection("servers");
        for (String servername : server.getKeys(false)) {
            ConfigurationSection cs = server.getConfigurationSection(servername);
            String displayname = cs.getString("displayname");
            ServerInfo si = new ServerInfo(servername, displayname);
            list.add(si);
        }
        return list;
    }

    public SignLayout getLayout(String layout) {
        return (SignLayout) this.signLayouts.get(layout);
    }

    public ServerInfo getServer(String server) {
        for (ServerInfo info : this.plugin.getConfigData().getServers()) {
            if (info.getName().equals(server)) {
                return info;
            }
        }
        return null;
    }

    public ServerSigns getPlugin() {
        return this.plugin;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public String getOfflineMessage() {
        return this.offlineMessage;
    }

    public List<ServerInfo> getServers() {
        return this.servers;
    }

    public Map<String, SignLayout> getSignLayouts() {
        return this.signLayouts;
    }

    public int getInterval() {
        return this.interval;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean isShowOfflineMsg() {
        return this.showOfflineMsg;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setPlugin(ServerSigns plugin) {
        this.plugin = plugin;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    public void setOfflineMessage(String offlineMessage) {
        this.offlineMessage = offlineMessage;
    }

    public void setServers(List<ServerInfo> servers) {
        this.servers = servers;
    }

    public void setSignLayouts(Map<String, SignLayout> signLayouts) {
        this.signLayouts = signLayouts;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setShowOfflineMsg(boolean showOfflineMsg) {
        this.showOfflineMsg = showOfflineMsg;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ConfigurationData)) return false;
        ConfigurationData other = (ConfigurationData) o;
        if (!other.canEqual(this)) return false;
        Object this$plugin = getPlugin();
        Object other$plugin = other.getPlugin();
        if (this$plugin == null ? other$plugin != null : !this$plugin.equals(other$plugin)) return false;
        Object this$config = getConfig();
        Object other$config = other.getConfig();
        if (this$config == null ? other$config != null : !this$config.equals(other$config)) return false;
        Object this$offlineMessage = getOfflineMessage();
        Object other$offlineMessage = other.getOfflineMessage();
        if (this$offlineMessage == null ? other$offlineMessage != null : !this$offlineMessage.equals(other$offlineMessage)) return false;
        Object this$servers = getServers();
        Object other$servers = other.getServers();
        if (this$servers == null ? other$servers != null : !this$servers.equals(other$servers)) return false;
        Object this$signLayouts = getSignLayouts();
        Object other$signLayouts = other.getSignLayouts();
        if (this$signLayouts == null ? other$signLayouts != null : !this$signLayouts.equals(other$signLayouts)) return false;
        if (getInterval() != other.getInterval()) return false;
        if (getTimeout() != other.getTimeout()) return false;
        if (isShowOfflineMsg() != other.isShowOfflineMsg()) return false;
        return getCooldown() == other.getCooldown();
    }

    public boolean canEqual(Object other) {
        return other instanceof ConfigurationData;
    }

    public int hashCode() {
        int result = 1;
        Object $plugin = getPlugin();
        result = result * 31 + ($plugin == null ? 0 : $plugin.hashCode());
        Object $config = getConfig();
        result = result * 31 + ($config == null ? 0 : $config.hashCode());
        Object $offlineMessage = getOfflineMessage();
        result = result * 31 + ($offlineMessage == null ? 0 : $offlineMessage.hashCode());
        Object $servers = getServers();
        result = result * 31 + ($servers == null ? 0 : $servers.hashCode());
        Object $signLayouts = getSignLayouts();
        result = result * 31 + ($signLayouts == null ? 0 : $signLayouts.hashCode());
        result = result * 31 + getInterval();
        result = result * 31 + getTimeout();
        result = result * 31 + (isShowOfflineMsg() ? 1231 : 1237);
        result = result * 31 + getCooldown();
        return result;
    }

    public String toString() {
        return "ConfigurationData(plugin=" + getPlugin() + ", config=" + getConfig() + ", offlineMessage=" + getOfflineMessage() + ", servers=" + getServers() + ", signLayouts=" + getSignLayouts()
                + ", interval=" + getInterval() + ", timeout=" + getTimeout() + ", showOfflineMsg=" + isShowOfflineMsg() + ", cooldown=" + getCooldown() + ")";
    }
}