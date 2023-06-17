package com.panstal.badplye.flamingcubediscord.MySQL.Entries;

public class PunishmentRecord {
    private String id;
    private String date;
    private String type;
    private String target;
    private String staff;
    private String reason;
    private String proof;
    private long expires;
    private boolean active;

    public PunishmentRecord(String id, String date, String type, String target, String staff, String reason, String proof, long expires, boolean active) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.target = target;
        this.staff = staff;
        this.reason = reason;
        this.proof = proof;
        this.expires = expires;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getStaff() {
        return staff;
    }

    public String getReason() {
        return reason;
    }

    public String getProof() {
        return proof;
    }

    public long getExpires() {
        return expires;
    }
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
