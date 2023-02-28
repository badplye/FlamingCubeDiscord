package com.oceanusmc.dev.badplye.oceanusdiscord.Modules.PlayerStats;

public class PlayerStats {
    private String playerUUID;
    private String rank;
    private long lastOnline;
    private long joinDate;
    private int kills;
    private long blocksBroken;
    private double balance;
    private long playTime;
    private String currentIsland;
    private String prestige;

    public PlayerStats(String playerUUID, String rank, long lastOnline, long joinDate, String currentIsland, String prestige, long playTime, int kills, long blocksBroken, double balance) {
        this.playerUUID = playerUUID;
        this.rank = rank;
        this.lastOnline = lastOnline;
        this.joinDate = joinDate;
        this.currentIsland = currentIsland;
        this.prestige = prestige;
        this.playTime = playTime;
        this.kills = kills;
        this.blocksBroken = blocksBroken;
        this.balance = balance;
    }


    public String getCurrentIsland() {
        return currentIsland;
    }

    public void setCurrentIsland(String currentIsland) {
        this.currentIsland = currentIsland;
    }

    public String getPrestige() {
        return prestige;
    }

    public void setPrestige(String prestige) {
        this.prestige = prestige;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(long blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
