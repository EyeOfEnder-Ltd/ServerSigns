package com.eyeofender.serversigns;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.eyeofender.serversigns.ping.ServerInfo;
import com.google.common.collect.Maps;

class PlayerListener implements Listener {
    private ServerSigns plugin;
    private Map<String, Long> cooldowns;

    public PlayerListener(ServerSigns plugin) {
        this.plugin = plugin;
        this.cooldowns = Maps.newHashMap();
    }

    @EventHandler
    private void onSignChange(SignChangeEvent e) {
        if ((e.getLine(0).equalsIgnoreCase("[Server]")) && (e.getPlayer().hasPermission("serversigns.create"))) {
            ServerInfo info = this.plugin.getConfigData().getServer(e.getLine(1));
            String layout = e.getLine(2);
            if (layout.equalsIgnoreCase("")) {
                layout = "default";
            }
            if (this.plugin.getConfigData().getLayout(layout) != null) {
                if (info != null) {
                    this.plugin.getDatabase().save(new TeleportSign(e.getLine(1), e.getBlock().getLocation(), layout));
                    this.plugin.loadSigns();
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Sign created.");
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + "Can't find this server!");
                }
            } else
                e.getPlayer().sendMessage(ChatColor.RED + "Can't find this layout!");
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        if ((e.getBlock().getState() instanceof Sign)) {
            TeleportSign ts = (TeleportSign) this.plugin.getDatabase().find(TeleportSign.class).where().eq("x", Double.valueOf(e.getBlock().getLocation().getX()))
                    .eq("y", Double.valueOf(e.getBlock().getLocation().getY())).eq("z", Double.valueOf(e.getBlock().getLocation().getZ())).findUnique();

            if ((ts != null) && (e.getPlayer().hasPermission("serversigns.destroy"))) {
                this.plugin.getDatabase().delete(ts);
                this.plugin.loadSigns();
                e.getPlayer().sendMessage(ChatColor.GREEN + "Sign destroyed.");
            }
        }
    }

    @EventHandler
    private void onClick(PlayerInteractEvent e) {
        if ((e.hasBlock()) && ((e.getClickedBlock().getState() instanceof Sign)) && (e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getPlayer().hasPermission("serversigns.use"))
                && (!hasCooldown(e.getPlayer().getName()))) for (TeleportSign ts : this.plugin.getSigns())
            if ((ts != null) && (ts.getLocation().equals(e.getClickedBlock().getLocation()))) {
                ServerInfo info = this.plugin.getConfigData().getServer(ts.getServer());
                if ((info != null) && (this.plugin.getConfigData().getLayout(ts.getLayout()).isTeleport())) if (info.isOnline()) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF("Connect");
                        out.writeUTF(ts.getServer());
                    } catch (IOException eee) {
                        Bukkit.getLogger().info("You'll never see me!");
                    }
                    e.getPlayer().sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
                } else if (this.plugin.getConfigData().isShowOfflineMsg()) {
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigData().getOfflineMessage()));
                }
            }
    }

    private boolean hasCooldown(String name) {
        long now = System.currentTimeMillis();
        if ((this.cooldowns.containsKey(name)) && (now - ((Long) this.cooldowns.get(name)).longValue() < this.plugin.getConfigData().getCooldown())) {
            return true;
        }

        this.cooldowns.put(name, Long.valueOf(now));
        return false;
    }
}