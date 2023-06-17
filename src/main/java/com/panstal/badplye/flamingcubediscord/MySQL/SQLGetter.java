package com.panstal.badplye.flamingcubediscord.MySQL;

import com.panstal.badplye.flamingcubediscord.Main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLGetter {
    protected Main plugin;
    public SQLGetter(Main plugin) {
        this.plugin = plugin;
    }
    public void createTable() {
        PreparedStatement ps;
        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS fcdiscord_punishments " + "(id VARCHAR(18), date VARCHAR(10), type VARCHAR(10),  target VARCHAR(20), staff VARCHAR(20), reason VARCHAR(2000), proof, VARCHAR(2000) expires VARCHAR(10))");
            ps.executeUpdate();
        } catch (SQLException e) {

        }
    }
}
