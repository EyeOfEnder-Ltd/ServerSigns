package com.eyeofender.serversigns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.eyeofender.serversigns.converter.Converter;
import com.eyeofender.serversigns.ping.Ping;

public class ServerSigns extends JavaPlugin {
    private List<TeleportSign> signs;
    private Ping ping;
    private ConfigurationData configData;
    private static ServerSigns instance;
    private long lastUpdate = System.currentTimeMillis();
    private int ticksAhead = 0;

    public ServerSigns() {
        instance = this;
    }

    public void onLoad() {
        Converter.convert(this);
    }

    public void onEnable() {
        this.configData = new ConfigurationData(this);
        this.configData.loadConfig();
        setupDB();
        loadSigns();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.ping = new Ping(this);
        Bukkit.getScheduler().runTaskAsynchronously(this, this.ping);
    }

    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(TeleportSign.class);
        return list;
    }

    public void loadSigns() {
        this.signs = getDatabase().find(TeleportSign.class).findList();
    }

    public void updateSigns(final List<TeleportSign> list) {
        long now = System.currentTimeMillis();
        if (now - this.lastUpdate < 50L) {
            this.ticksAhead += 1;
        } else {
            this.ticksAhead = 1;
        }
        this.lastUpdate = now;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                Iterator<TeleportSign> tempIterator = list.iterator();
                while (tempIterator.hasNext())
                    ((TeleportSign) tempIterator.next()).updateSign();
            }
        }, this.ticksAhead);
    }

    private void setupDB() {
        try {
            getDatabase().find(TeleportSign.class).findRowCount();
        } catch (PersistenceException ex) {
            getLogger().log(Level.INFO, "Installing database due to first time usage");
            installDDL();
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("serversigns")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("serversigns.reload")) {
                    sender.sendMessage(ChatColor.GREEN + "Reloading configuration from disk");
                    getConfigData().reloadConfig();
                }
            } else {
                sender.sendMessage("Running " + getDescription().getFullName());
            }
            return true;
        }
        return false;
    }

    public List<TeleportSign> getSigns() {
        return this.signs;
    }

    public ConfigurationData getConfigData() {
        return this.configData;
    }

    public static ServerSigns getInstance() {
        return instance;
    }
}
