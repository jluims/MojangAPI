package net.brxen.mojangapi;

import java.util.UUID;

public class Player {

    private final UUID uuid;
    private final String username;
    private final String accessToken;

    public Player(UUID uuid, String username, String accessToken) {
        this.uuid = uuid;
        this.username = username;
        this.accessToken = accessToken;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
