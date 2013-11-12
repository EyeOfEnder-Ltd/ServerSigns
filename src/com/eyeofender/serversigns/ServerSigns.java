package com.eyeofender.serversigns;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.eyeofender.serversigns.converter.Converter;
import com.eyeofender.serversigns.ping.BungeePing;

public class ServerSigns extends JavaPlugin {
    private List<TeleportSign> signs;
    private ConfigurationData configData;
    private static ServerSigns instance;

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

        final BungeePing bPing = new BungeePing(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bPing);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                bPing.pingAll();
            }
        }, 20, getConfigData().getInterval() * 20);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(TeleportSign.class);
        return list;
    }

    public void loadSigns() {
        this.signs = getDatabase().find(TeleportSign.class).findList();
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
