package com.eyeofender.serversigns.manager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.eyeofender.serversigns.ServerSigns;
import com.eyeofender.serversigns.ping.ServerInfo;
import com.google.common.collect.Maps;

public class SignManager {

    private static FileConfiguration config = null;
    private static File configFile = null;
    private static Map<String, String> signs = Maps.newHashMap();

    private SignManager() {
    }

    public static void saveSign(Location location, String server) {
        signs.put(parseLocation(location), server);
    }

    public static void removeSign(Location location) {
        signs.remove(parseLocation(location));
    }

    public static void updateSign(Location location) {
        ServerInfo info = ServerSigns.getInstance().getConfigManager().getServer(signs.get(parseLocation(location)));
        if (info == null) return;

        if (!location.getWorld().getChunkAt(location).isLoaded()) return;
        Block b = location.getBlock();
        if (!(b.getState() instanceof Sign)) return;

        Sign s = (Sign) b.getState();

        s.setLine(0, info.getDisplayName());
        s.setLine(1, "");
        s.setLine(2, info.getThirdLine());
        s.setLine(3, info.getFourthLine());

        s.update();
    }

    public static void connect(Location location, Player player) {
        ServerInfo info = ServerSigns.getInstance().getConfigManager().getServer(signs.get(parseLocation(location)));
        if (info == null) return;

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(info.getName());
        } catch (IOException eee) {
            Bukkit.getLogger().info("You'll never see me!");
        }
        player.sendPluginMessage(ServerSigns.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void updateSigns() {
        ConfigurationSection section = getConfig().getConfigurationSection("signs");
        if (section == null) return;

        for (String location : signs.keySet()) {
            updateSign(parseLocation(location));
        }
    }

    public static void reloadConfig() {
        if (configFile == null) configFile = new File(ServerSigns.getInstance().getDataFolder(), "signs.dat");
        config = YamlConfiguration.loadConfiguration(configFile);

        signs.clear();
        ConfigurationSection section = config.getConfigurationSection("signs");
        if (section == null) return;

        for (String location : section.getKeys(false)) {
            String server = config.getString(location);
            if (server == null) continue;
            signs.put(location, server);
        }
    }

    public static FileConfiguration getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    public static void saveConfig() {
        if (config == null || configFile == null) return;

        config.createSection("signs", signs);

        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            ServerSigns.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    private static String parseLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static Location parseLocation(String loc) {
        String[] parts = loc.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

}
