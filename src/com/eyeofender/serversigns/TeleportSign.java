package com.eyeofender.serversigns;

import java.util.List;
import java.util.logging.Level;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.eyeofender.serversigns.ping.ServerInfo;

@Entity
@Table(name = "lobby_teleportsigns")
public class TeleportSign {

    @Id
    private int id;

    @NotEmpty
    private String server;

    @NotEmpty
    private String layout;

    @NotEmpty
    private String worldName;

    @NotNull
    private double x;

    @NotNull
    private double y;

    @NotNull
    private double z;

    public TeleportSign() {
    }

    public TeleportSign(String server, Location loc, String layout) {
        this.server = server;
        this.layout = layout;
        setLocation(loc);
    }

    private void setLocation(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location getLocation() {
        World welt = Bukkit.getServer().getWorld(this.worldName);
        return new Location(welt, this.x, this.y, this.z);
    }

    public void updateSign() {
        Location location = getLocation();
        if (location.getWorld().getChunkAt(location).isLoaded()) {
            Block b = location.getBlock();
            if ((b.getState() instanceof Sign)) {
                ServerInfo sinfo = ServerSigns.getInstance().getConfigData().getServer(this.server);
                SignLayout signLayout = ServerSigns.getInstance().getConfigData().getLayout(this.layout);
                if (signLayout != null) {
                    Sign s = (Sign) b.getState();
                    List<String> lines = signLayout.parseLayout(sinfo);
                    for (int i = 0; i < signLayout.getLines().size(); i++) {
                        s.setLine(i, lines.get(i));
                    }
                    s.update();
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "[TeleportSigns] can't find layout '{0}'", this.layout);
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public String getServer() {
        return this.server;
    }

    public String getLayout() {
        return this.layout;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TeleportSign)) return false;
        TeleportSign other = (TeleportSign) o;
        if (!other.canEqual(this)) return false;
        if (getId() != other.getId()) return false;
        Object this$server = getServer();
        Object other$server = other.getServer();
        if (this$server == null ? other$server != null : !this$server.equals(other$server)) return false;
        Object this$layout = getLayout();
        Object other$layout = other.getLayout();
        if (this$layout == null ? other$layout != null : !this$layout.equals(other$layout)) return false;
        Object this$worldName = getWorldName();
        Object other$worldName = other.getWorldName();
        if (this$worldName == null ? other$worldName != null : !this$worldName.equals(other$worldName)) return false;
        if (Double.compare(getX(), other.getX()) != 0) return false;
        if (Double.compare(getY(), other.getY()) != 0) return false;
        return Double.compare(getZ(), other.getZ()) == 0;
    }

    public boolean canEqual(Object other) {
        return other instanceof TeleportSign;
    }

    public int hashCode() {
        int result = 1;
        result = result * 31 + getId();
        Object $server = getServer();
        result = result * 31 + ($server == null ? 0 : $server.hashCode());
        Object $layout = getLayout();
        result = result * 31 + ($layout == null ? 0 : $layout.hashCode());
        Object $worldName = getWorldName();
        result = result * 31 + ($worldName == null ? 0 : $worldName.hashCode());
        long $x = Double.doubleToLongBits(getX());
        result = result * 31 + (int) ($x >>> 32 ^ $x);
        long $y = Double.doubleToLongBits(getY());
        result = result * 31 + (int) ($y >>> 32 ^ $y);
        long $z = Double.doubleToLongBits(getZ());
        result = result * 31 + (int) ($z >>> 32 ^ $z);
        return result;
    }

    public String toString() {
        return "TeleportSign(id=" + getId() + ", server=" + getServer() + ", layout=" + getLayout() + ", worldName=" + getWorldName() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ")";
    }
}
