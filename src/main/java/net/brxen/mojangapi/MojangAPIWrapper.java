package net.brxen.mojangapi;

import com.google.gson.*;
import net.brxen.mojangapi.exception.MojangAPIException;
import net.brxen.mojangapi.entry.NameHistoryEntry;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MojangAPIWrapper {

    private final String userAgent;
    public final HashMap<String, UUID> cachedUUIDs = new HashMap<>();
    public MojangAPIWrapper(String userAgent) {
        this.userAgent = userAgent;
    }

    private Map.Entry<String, UUID> getCachedEntry(UUID uuid) {
        for (Map.Entry<String, UUID> entry : cachedUUIDs.entrySet()) {
            if (entry.getValue() == uuid) {
                return entry;
            }
        }
        return null;
    }

    private Map.Entry<String, UUID> getCachedEntry(String name) {
        for (Map.Entry<String, UUID> entry : cachedUUIDs.entrySet()) {
            if (entry.getKey().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    private void cacheUUID(String username, UUID uuid) {
        cachedUUIDs.put(username, uuid);
    }

    /**
     * Returns the statuses of the Mojang APIs
     * @return Array of statuses
     */
    public Status[] getAPIStatuses() {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://status.mojang.com/check")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseStr.append(line);
            }

            JsonArray responseObj = new JsonParser().parse(responseStr.toString()).getAsJsonArray();

            Status status1 = Status.fromString(responseObj.get(0).getAsJsonObject().get("minecraft.net").getAsString());
            Status status2= Status.fromString(responseObj.get(0).getAsJsonObject().get("session.minecraft.net").getAsString());
            Status status3 = Status.fromString(responseObj.get(0).getAsJsonObject().get("account.mojang.com").getAsString());
            Status status4 = Status.fromString(responseObj.get(0).getAsJsonObject().get("authserver.mojang.com").getAsString());
            Status status5 = Status.fromString(responseObj.get(0).getAsJsonObject().get("sessionserver.mojang.com").getAsString());
            Status status6 = Status.fromString(responseObj.get(0).getAsJsonObject().get("api.mojang.com").getAsString());
            Status status7 = Status.fromString(responseObj.get(0).getAsJsonObject().get("textures.minecraft.net").getAsString());
            Status status8 = Status.fromString(responseObj.get(0).getAsJsonObject().get("mojang.com").getAsString());

            return new Status[] {status1, status2, status3, status4, status5, status6, status7, status8};
        } catch (IOException ex) {
            return null;
        }
    }

    public String getUsername(UUID uuid) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString())).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String content = Utils.readInputStream(connection.getInputStream());
            try {
                JsonObject responseObj = new JsonParser().parse(content).getAsJsonObject();
                String username = responseObj.get("name").getAsString();
                return username;
            } catch (JsonParseException ex) {
                throw new MojangAPIException(ex.getMessage());
            }
        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    /**
     * Fetches a UUID from a username using Mojang's APIs
     * @param username Username that pertains to UUID
     * @return The UUID that pertains to the username
     */
    public UUID fromUsername(String username) throws MojangAPIException {
        if (getCachedEntry(username) != null) {
            return getCachedEntry(username).getValue();
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/users/profiles/minecraft/" + username)).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String content = Utils.readInputStream(connection.getInputStream());
            try {
                JsonObject responseObj = new JsonParser().parse(content).getAsJsonObject();
                UUID uuid = Utils.fromUndashed(responseObj.get("id").getAsString());
                cacheUUID(username, uuid);
                return uuid;
            } catch (JsonParseException ex) {
                throw new MojangAPIException(ex.getMessage());
            }
        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }


    /**
     * Fetches a key,value pair of usernames and uuids from the Mojang API
     * @param usernames Array of usernames used to fetch uuids
     * @return UUIDs fetched from the API
     * @throws IOException Invalid input
     */
    public HashMap<String, UUID> fromUsernames(String[] usernames) throws MojangAPIException {
        if (usernames.length > 10) {
            throw new MojangAPIException("You can only check 10 usernames per request!");
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/profiles/minecraft")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            JsonArray requestObj = new JsonArray();
            for (String username : usernames) {
                requestObj.add(username);
            }

            Utils.writeOutputStream(connection.getOutputStream(), requestObj.toString());

            String responseStr = Utils.readInputStream(connection.getInputStream());
            JsonArray responseObj = new JsonParser().parse(responseStr).getAsJsonArray();
            HashMap<String, UUID> uuidList = new HashMap<>(responseObj.size());

            for (int i = responseObj.size() - 1; i >= 0; i--) {
                JsonObject obj = responseObj.get(i).getAsJsonObject();
                cacheUUID(obj.get("name").getAsString(), Utils.fromUndashed(obj.get("id").getAsString()));
                uuidList.put(obj.get("name").getAsString(), Utils.fromUndashed(obj.get("id").getAsString()));
            }

            return uuidList;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    /**
     * Fetches a list of NameHistoryEntries from the API
     * Entries are sorted from oldest to newest
     * @param uuid UUID used to fetch NameHistoryEntries
     * @return List of NameHistoryEntries received from the API
     */
    public List<NameHistoryEntry> fromUUID(UUID uuid) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/user/profiles/" + Utils.toUndashed(uuid) + "/names")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String responseStr = Utils.readInputStream(connection.getInputStream());
            JsonArray responseObj = new JsonParser().parse(responseStr).getAsJsonArray();

            List<NameHistoryEntry> nameEntries = new ArrayList<>();

            for (int i = 0; i < responseObj.size(); i++) {
                JsonObject obj = responseObj.get(i).getAsJsonObject();
                Long date = null;
                if (obj.has("changedToAt")) {
                    date = obj.get("changedToAt").getAsLong();
                }
                nameEntries.add(new NameHistoryEntry(obj.get("name").getAsString(), date));
            }

            return nameEntries;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    /**
     * Returns the player's skin URL
     * @param uuid
     * @return Entry of skin and cape URL (cape may be null)
     */
    public String getSkinUrl(UUID uuid) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + Utils.toUndashed(uuid))).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            if (connection.getResponseCode() != 200) {
                return null;
            }

            String response = Utils.readInputStream(connection.getInputStream());
            String dataAsBase64 = new JsonParser().parse(response).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
            JsonObject data = new JsonParser().parse(new String(Base64.getDecoder().decode(dataAsBase64), StandardCharsets.UTF_8)).getAsJsonObject();
            return data.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public String getCapeUrl(UUID uuid) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + Utils.toUndashed(uuid))).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            if (connection.getResponseCode() != 200) {
                return null;
            }

            String response = Utils.readInputStream(connection.getInputStream());
            String dataAsBase64 = new JsonParser().parse(response).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
            JsonObject data = new JsonParser().parse(new String(Base64.getDecoder().decode(dataAsBase64), StandardCharsets.UTF_8)).getAsJsonObject();
            String capeUrl = data.get("textures").getAsJsonObject().has("CAPE") ? data.get("textures").getAsJsonObject().get("CAPE").getAsJsonObject().get("url").getAsString() : null;
            return capeUrl;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    /**
     *
     * @return List of SHA-256 hashes for blocked server IPs
     */
    public String[] getBlockedServerHashes() throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://sessionserver.mojang.com/blockedservers")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String content = Utils.readInputStream(connection.getInputStream());

            return content.split("\n");

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public boolean resetSkin(UUID uuid, String accessToken) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/user/profile/" + Utils.toUndashed(uuid) + "/skin")).openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            return connection.getResponseCode() == 204;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public boolean verifySecurityLocation(String accessToken) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/user/security/location")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            return connection.getResponseCode() == 204;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public boolean changeSkin(String accessToken, SkinType skinType, URL skinUrl) throws MojangAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.minecraftservices.com/minecraft/profile/skins")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            JsonObject req = new JsonObject();
            req.addProperty("variant", skinType.asString());
            req.addProperty("url", skinUrl.toString());
            Utils.writeOutputStream(connection.getOutputStream(), req.toString());
            return connection.getResponseCode() == 204 || connection.getResponseCode() == 200;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

}
