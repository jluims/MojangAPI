package net.brxen.mojangapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangAuthWrapper {

    private static final String BASE_PATH = "https://authserver.mojang.com";
    private final String clientToken;
    private final String userAgent;

    public MojangAuthWrapper(String clientToken, String userAgent) {
        this.clientToken = clientToken;
        this.userAgent = userAgent;
    }

    public Player authenticate(String username, String password) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL(BASE_PATH + "/authenticate")).openConnection();
            JsonObject requestObj = new JsonObject();
            JsonObject agent = new JsonObject();
            agent.addProperty("name", "Minecraft");
            agent.addProperty("version", 1);
            requestObj.add("agent", agent);
            requestObj.addProperty("username", username);
            requestObj.addProperty("password", password);
            requestObj.addProperty("clientToken", this.clientToken);
            requestObj.addProperty("requestUser", true);

            // Set connection flags
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(requestObj.toString());
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseStr.append(line);
            }

            JsonObject responseObj = JsonParser.parseString(responseStr.toString()).getAsJsonObject();

            return new Player(Utils.fromUndashed(responseObj.get("selectedProfile").getAsJsonObject().get("id").getAsString()), responseObj.get("selectedProfile").getAsJsonObject().get("name").getAsString(), responseObj.get("accessToken").getAsString());

        } catch (IOException ignored) {

        }
        return null;
    }

    public Player refresh(String accessToken) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL(BASE_PATH + "/refresh")).openConnection();
            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("accessToken", accessToken);
            requestObj.addProperty("clientToken", this.clientToken);
            requestObj.addProperty("requestUser", true);

            // Set connection flags
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(requestObj.toString());
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseStr.append(line);
            }

            JsonObject responseObj = JsonParser.parseString(responseStr.toString()).getAsJsonObject();

            return new Player(Utils.fromUndashed(responseObj.get("selectedProfile").getAsJsonObject().get("id").getAsString()), responseObj.get("selectedProfile").getAsJsonObject().get("name").getAsString(), responseObj.get("accessToken").getAsString());

        } catch (IOException ignored) {
        }

        return null;
    }

    public boolean validate(String accessToken) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL(BASE_PATH + "/validate")).openConnection();
            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("accessToken", accessToken);
            requestObj.addProperty("clientToken", this.clientToken);

            // Set connection flags
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(requestObj.toString());
            writer.flush();

            return true;

        } catch (IOException ignored) {
        } // 403 Gets thrown which triggers an IOException

        return false;
    }

    public boolean signout(String username, String password) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL(BASE_PATH + "/signout")).openConnection();
            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("username", username);
            requestObj.addProperty("password", password);

            // Set connection flags
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(requestObj.toString());
            writer.flush();

            return true;

        } catch (IOException ignored) {
        } // 403 Gets thrown which triggers an IOException

        return false;
    }

    public boolean invalidate(String accessToken) {
        try {
            HttpURLConnection connection = (HttpURLConnection) Utils.escapeURL(new URL(BASE_PATH + "/invalidate")).openConnection();
            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("accessToken", accessToken);
            requestObj.addProperty("clientToken", this.clientToken);

            // Set connection flags
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(requestObj.toString());
            writer.flush();

            return true;

        } catch (IOException ignored) {
        } // 403 Gets thrown which triggers an IOException

        return false;
    }


}
