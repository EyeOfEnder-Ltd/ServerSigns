package com.eyeofender.serversigns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.eyeofender.serversigns.manager.ConfigManager;
import com.eyeofender.serversigns.manager.SignManager;
import com.eyeofender.serversigns.ping.Ping;
import com.eyeofender.serversigns.ping.ServerInfo;

public class ServerSigns extends JavaPlugin {
    private static ServerSigns instance;

    private ConfigManager configManager;
    private Ping ping;

    public ServerSigns() {
        instance = this;
    }

    public void onEnable() {
        this.configManager = new ConfigManager(this);
        configManager.saveDefault();
        configManager.load();

        SignManager.reloadConfig();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        ping = new Ping(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                ping.pingAll();
            }
        }, 20, getConfigManager().getInterval() * 20);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        SignManager.saveConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("serversigns")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("serversigns.reload")) {
                    sender.sendMessage(ChatColor.GREEN + "Reloading configuration from disk");
                    getConfigManager().load();
                } else if (args[0].equalsIgnoreCase("ping")) {
                    if (args.length > 1) {
                        ServerInfo info = configManager.getServer(args[1]);
                        if (info != null) {
                            ping.ping(info);
                            sender.sendMessage(info.getDisplayName());
                            sender.sendMessage(info.getDescription());
                            sender.sendMessage(info.getOnlinePlayers() + "/" + info.getMaxPlayers());
                        } else {
                            sender.sendMessage(ChatColor.RED + "Invalid server.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please specify a server.");
                    }
                }
            } else {
                sender.sendMessage("Running " + getDescription().getFullName());
            }
            return true;
        }
        return false;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static ServerSigns getInstance() {
        return instance;
    }
}
