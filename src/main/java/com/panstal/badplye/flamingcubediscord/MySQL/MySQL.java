package com.panstal.badplye.flamingcubediscord.MySQL;

import com.panstal.badplye.flamingcubediscord.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private Connection connection;

    public MySQL(Main plugin) {
        host = plugin.getConfig().getString("db-host");
        port = plugin.getConfig().getString("db-port");

        database = plugin.getConfig().getString("db-database");
        username = plugin.getConfig().getString("db-username");
        password = plugin.getConfig().getString("db-password");
    }


    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if(!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
