package net.brxen.mojangapi;

import com.google.gson.*;
import net.brxen.mojangapi.entry.NameHistoryEntry;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

            JsonArray responseObj = JsonParser.parseString(responseStr.toString()).getAsJsonArray();

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

    /**
     * Fetches a UUID from a username using Mojang's APIs
     * @param username Username that pertains to UUID
     * @return The UUID that pertains to the username
     */
    public UUID fromUsername(String username) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/users/profiles/minecraft/" + username)).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String content = Utils.readInputStream(connection.getInputStream());
            try {
                JsonObject responseObj = JsonParser.parseString(content).getAsJsonObject();
                UUID uuid = Utils.fromUndashed(responseObj.get("id").getAsString());
                cacheUUID(username, uuid);
                return uuid;
            } catch (JsonParseException ignored) { }
        } catch (IOException ignored) {}
        return null;
    }


    /**
     * Fetches a key,value pair of usernames and uuids from the Mojang API
     * @param usernames Array of usernames used to fetch uuids
     * @return UUIDs fetched from the API
     * @throws IOException Invalid input
     */
    public HashMap<String, UUID> fromUsernames(String[] usernames) throws IOException {
        if (usernames.length > 10) {
            throw new IOException("You can only check 10 usernames per request!");
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
            JsonArray responseObj = JsonParser.parseString(responseStr).getAsJsonArray();
            HashMap<String, UUID> uuidList = new HashMap<>(responseObj.size());

            for (int i = responseObj.size() - 1; i >= 0; i--) {
                JsonObject obj = responseObj.get(i).getAsJsonObject();
                cacheUUID(obj.get("name").getAsString(), Utils.fromUndashed(obj.get("id").getAsString()));
                uuidList.put(obj.get("name").getAsString(), Utils.fromUndashed(obj.get("id").getAsString()));
            }

            return uuidList;

        } catch (IOException ignored) { }
        return null;
    }

    /**
     * Fetches a list of NameHistoryEntries from the API
     * Entries are sorted from oldest -> newest
     * @param uuid UUID used to fetch NameHistoryEntries
     * @return List of NameHistoryEntries received from the API
     */
    public List<NameHistoryEntry> fromUUID(UUID uuid) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL("https://api.mojang.com/user/profiles/" + Utils.toUndashed(uuid) + "/names")).openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String responseStr = Utils.readInputStream(connection.getInputStream());
            JsonArray responseObj = JsonParser.parseString(responseStr).getAsJsonArray();

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

        } catch (IOException ignored) {}
        return null;
    }
}
