package com.panstal.badplye.flamingcubediscord.MySQL;

import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PunishmentGetter extends SQLGetter {
    public PunishmentGetter(Main plugin) {
        super(plugin);
    }

    public boolean exists() {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT id FROM fcdiscord_punishments WHERE id=?");
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    public PunishmentRecord getPunishment(String id) throws SQLException {
        PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM fcdiscord_punishments WHERE id LIKE ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return new PunishmentRecord(
                    rs.getString("id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getString("target"),
                    rs.getString("staff"),
                    rs.getString("reason"),
                    rs.getString("proof"),
                    rs.getLong("expires"),
                    rs.getBoolean("active")
            );
        }
        return null;
    }

    public void createPunishment(PunishmentRecord record) {
        try {
            if(!exists()) {
                PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("INSERT INTO fcdiscord_punishments" + " (id, date, type, target, staff, reason, proof, expires, active) VALUES (?,?,?,?,?,?,?,?,?)");
                ps.setString(1, record.getId());
                ps.setString(2, record.getDate());
                ps.setString(3, record.getType());
                ps.setString(4, record.getTarget());
                ps.setString(5, record.getStaff());
                ps.setString(6, record.getReason());
                ps.setString(7, record.getProof());
                ps.setLong(8, record.getExpires());
                ps.setBoolean(9, record.getActive());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePunishment(PunishmentRecord record) {
        try {
            if(!exists()) {
                PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE fcdiscord_punishments SET date = ?, type = ?, target = ?, staff = ?, reason = ?, proof = ?, expires = ?, active = ? WHERE id = ?");
                ps.setString(1, record.getDate());
                ps.setString(2, record.getType());
                ps.setString(3, record.getTarget());
                ps.setString(4, record.getStaff());
                ps.setString(5, record.getReason());
                ps.setString(6, record.getProof());
                ps.setLong(7, record.getExpires());
                ps.setBoolean(8, record.getActive());
                ps.setString(9, record.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<PunishmentRecord> targetLookup(String id) throws ClassNotFoundException, SQLException {
        ResultSet rs;
        PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM fcdiscord_punishments WHERE target LIKE ?");
        ps.setString(1, id);
        rs = ps.executeQuery();
        ArrayList<PunishmentRecord> punishmentList = new ArrayList<>();
        while (rs.next()) {
            PunishmentRecord record = new PunishmentRecord(
                    rs.getString("id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getString("target"),
                    rs.getString("staff"),
                    rs.getString("reason"),
                    rs.getString("proof"),
                    rs.getLong("expires"),
                    rs.getBoolean("active")
            );
            punishmentList.add(record);
        }
        return punishmentList;
    }
    public ArrayList<PunishmentRecord> unPunishLookup() throws ClassNotFoundException, SQLException {
        ResultSet rs;
        PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM fcdiscord_punishments WHERE expires <= ? AND active = 1");
        ps.setString(1, String.valueOf(System.currentTimeMillis() / 1000));
        rs = ps.executeQuery();
        ArrayList<PunishmentRecord> punishmentList = new ArrayList<>();
        while (rs.next()) {
            PunishmentRecord record = new PunishmentRecord(
                    rs.getString("id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getString("target"),
                    rs.getString("staff"),
                    rs.getString("reason"),
                    rs.getString("proof"),
                    rs.getLong("expires"),
                    rs.getBoolean("active")
            );
            punishmentList.add(record);
        }
        return punishmentList;
    }
}
