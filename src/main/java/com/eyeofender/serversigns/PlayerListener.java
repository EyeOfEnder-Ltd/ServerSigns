package com.eyeofender.serversigns;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.eyeofender.serversigns.manager.SignManager;
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
        if (e.getLine(0).equalsIgnoreCase("[Server]") && e.getPlayer().hasPermission("serversigns.create")) {
            String server = e.getLine(1);
            if (plugin.getConfigManager().getServer(server) != null) {
                SignManager.saveSign(e.getBlock().getLocation(), e.getLine(1));
                e.getPlayer().sendMessage(ChatColor.GREEN + "Sign created.");
            } else {
                e.getPlayer().sendMessage(ChatColor.RED + "Unknown server!");
            }

        } else {
            if (e.isCancelled()) return;
            e.setLine(0, ChatColor.translateAlternateColorCodes('&', e.getLine(0)));
            e.setLine(1, ChatColor.translateAlternateColorCodes('&', e.getLine(1)));
            e.setLine(2, ChatColor.translateAlternateColorCodes('&', e.getLine(2)));
            e.setLine(3, ChatColor.translateAlternateColorCodes('&', e.getLine(3)));
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Sign) {
            if (!e.getPlayer().hasPermission("serversigns.destroy")) e.setCancelled(true);

            SignManager.removeSign(e.getBlock().getLocation(), plugin.getConfigManager().getServerFromDisplay(((Sign) e.getBlock().getState()).getLine(0)).getName());
            e.getPlayer().sendMessage(ChatColor.GREEN + "Sign destroyed.");
        }
    }

    @EventHandler
    private void onClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign)) return;

        Player player = e.getPlayer();
        if (!player.hasPermission("serversigns.use")) return;
        if (hasCooldown(player.getName())) return;

        Action action = e.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || (!player.hasPermission("serversigns.destroy") && action == Action.LEFT_CLICK_BLOCK)) {
            SignManager.connect(block.getLocation(), player);
        }
    }

    private boolean hasCooldown(String name) {
        long now = System.currentTimeMillis();
        if ((cooldowns.containsKey(name)) && (now - ((Long) cooldowns.get(name)).longValue() < plugin.getConfigManager().getCooldown())) {
            return true;
        }

        cooldowns.put(name, Long.valueOf(now));
        return false;
    }
}