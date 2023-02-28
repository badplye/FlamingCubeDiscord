package com.oceanusmc.dev.badplye.oceanusdiscord.Utilities.Database;

import com.oceanusmc.dev.badplye.oceanusdiscord.OceanusDiscord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public abstract class Database {
    protected Connection connection = null;
    protected OceanusDiscord mcPlugin;
    protected static final Logger log = Logger.getLogger("Minecraft");
    public Connection getConnection() throws SQLException {

        //Try to connect to my MySQL database running locally
        String url = "jdbc:mysql://" + mcPlugin.getConfig().getString("db-host") + ":" + mcPlugin.getConfig().getString("db-port") + "/" + mcPlugin.getConfig().getString("db-database") + "?useSSL=false";
        String user = mcPlugin.getConfig().getString("db-username");
        String password = mcPlugin.getConfig().getString("db-password");

        Connection connection = DriverManager.getConnection(url, user, password);

        this.connection = connection;

        return connection;
    }

    public abstract void initializeDatabase() throws SQLException;

}
