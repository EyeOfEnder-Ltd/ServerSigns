package com.eyeofender.serversigns.manager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SignManager {

    private static FileConfiguration config = null;
    private static File configFile = null;
    private static Map<String, List<String>> signs = Maps.newHashMap();

    private SignManager() {
    }

    public static void saveSign(Location location, String server) {
        List<String> locations = signs.get(server);
        if (locations == null) locations = Lists.newArrayList();
        locations.add(parseLocation(location));
        signs.put(server, locations);
    }

    public static void removeSign(Location location, String server) {
        List<String> locations = signs.get(server);
        if (locations == null) return;
        locations.remove(parseLocation(location));
        signs.put(server, locations);
    }

    public static void updateSigns() {
        for (ServerInfo info : ServerSigns.getInstance().getConfigManager().getServers()) {
            updateSigns(info);
        }
    }

    public static void updateSigns(ServerInfo info) {
        List<String> locations = signs.get(info.getName());
        if (locations == null || locations.isEmpty()) return;

        for (String loc : locations) {
            Location location = parseLocation(loc);
            if (!location.getWorld().getChunkAt(location).isLoaded()) return;
            Block b = location.getBlock();
            if (!(b.getState() instanceof Sign)) return;

            Sign s = (Sign) b.getState();

            s.setLine(0, info.getFirstLine());
            s.setLine(1, info.getSecondLine());
            s.setLine(2, info.getThirdLine());
            s.setLine(3, info.getFourthLine());
            s.update();
        }
    }

    public static void connect(Location location, Player player) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign)) return;
        String name = ((Sign) block.getState()).getLine(0);

        ServerInfo info = ServerSigns.getInstance().getConfigManager().getServerFromDisplay(name);
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

    public static void reloadConfig() {
        if (configFile == null) configFile = new File(ServerSigns.getInstance().getDataFolder(), "signs.dat");
        config = YamlConfiguration.loadConfiguration(configFile);

        signs.clear();
        ConfigurationSection section = config.getConfigurationSection("signs");
        if (section == null) return;

        for (String server : section.getKeys(false)) {
            List<String> locations = config.getStringList("signs." + server);
            if (locations == null || locations.isEmpty()) continue;
            signs.put(server, locations);
        }
    }

    public static FileConfiguration getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    public static void saveConfig() {
        if (config == null || configFile == null) return;

        for (Entry<String, List<String>> entry : signs.entrySet()) {
            config.set("signs." + entry.getKey(), entry.getValue());
        }

        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            ServerSigns.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public static boolean hasSign(ServerInfo info) {
        List<String> locations = signs.get(info);
        return locations != null && !locations.isEmpty();
    }

    private static String parseLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static Location parseLocation(String loc) {
        String[] parts = loc.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

}
