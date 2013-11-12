package com.eyeofender.serversigns.converter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.eyeofender.serversigns.ServerSigns;

public class Converter {
    public static void convert(ServerSigns plugin) {
        Logger log = Bukkit.getLogger();
        String location = plugin.getDataFolder().getAbsolutePath() + File.separator + plugin.getName() + ".db";
        File file = new File(location);
        if (file.exists()) try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(location);
            Statement stat = conn.createStatement();
            try {
                stat.execute("ALTER TABLE lobby_teleportsigns ADD layout CHAR(255)");
                stat.close();
                stat = conn.createStatement();
                stat.executeUpdate("UPDATE lobby_teleportsigns SET layout='default' WHERE layout IS null");
                stat.close();
                conn.close();
            } catch (SQLException ex) {
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
