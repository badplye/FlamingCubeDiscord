package com.oceanusmc.dev.badplye.oceanusdiscord.Utilities.Database;

import com.oceanusmc.dev.badplye.oceanusdiscord.Modules.PlayerStats.PlayerStats;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PunishmentDB extends Database {
    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();

        //Create the player_stats table
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (id varchar(18) primary key, target varchar(22), staff varchar(22), join_date BIGINT, current_island varchar(20), prestige varchar(3), play_time BIGINT, kills int, blocks_broken BIGINT, balance double)";

        statement.execute(sql);

        statement.close();

    }

    public PlayerStats findPlayerStatsByUUID(String uuid) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM player_stats WHERE uuid=?");
        statement.setString(1, uuid);

        ResultSet resultSet = statement.executeQuery();

        PlayerStats playerStats;

        if(resultSet.next()){
            playerStats = new PlayerStats(resultSet.getString("uuid"), resultSet.getString("rank"), resultSet.getLong("last_online"), resultSet.getLong("join_date"), resultSet.getString("current_island"), resultSet.getString("prestige"), resultSet.getLong("play_time"), resultSet.getInt("kills"), resultSet.getLong("blocks_broken"), resultSet.getDouble("balance"));

            statement.close();

            return playerStats;
        }

        statement.close();

        return null;
    }
    public void createPlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_stats(uuid, rank, last_online, join_date, current_island, prestige, play_time, kills, blocks_broken, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, playerStats.getPlayerUUID());
        statement.setString(2, playerStats.getRank());
        statement.setLong(3, playerStats.getLastOnline());
        statement.setLong(4, playerStats.getJoinDate());
        statement.setString(5, playerStats.getCurrentIsland());
        statement.setString(6, playerStats.getPrestige());
        statement.setLong(7, playerStats.getPlayTime());
        statement.setLong(8, playerStats.getKills());
        statement.setLong(9, playerStats.getBlocksBroken());
        statement.setDouble(10, playerStats.getBalance());


        statement.executeUpdate();

        statement.close();

    }

    public void updatePlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE player_stats SET rank = ?, last_online = ?, play_time = ?, current_island = ?, prestige = ?, kills = ?, blocks_broken = ?, balance = ? WHERE uuid = ?");
        statement.setString(1, playerStats.getRank());
        statement.setLong(2, playerStats.getLastOnline());
        statement.setLong(3, playerStats.getPlayTime());
        statement.setString(4, playerStats.getCurrentIsland());
        statement.setString(5, playerStats.getPrestige());
        statement.setLong(6, playerStats.getKills());
        statement.setLong(7, playerStats.getBlocksBroken());
        statement.setDouble(8, playerStats.getBalance());
        statement.setString(9, playerStats.getPlayerUUID());


        statement.executeUpdate();

        statement.close();

    }
}
