package net.brxen.mojangapi;

import com.google.gson.JsonObject;
import net.brxen.mojangapi.exception.MojangAPIException;

import java.io.*;
import java.net.URL;

public class MojangAuthWrapper {

    private static final String BASE_PATH = "https://authserver.mojang.com";
    private final String clientToken;
    private final String userAgent;

    public MojangAuthWrapper(String clientToken, String userAgent) {
        this.clientToken = clientToken;
        this.userAgent = userAgent;
    }

    public Player authenticate(String username, String password) throws MojangAPIException {
        try {
            APIHelper helper = new APIHelper(new URL(BASE_PATH + "/authenticate"));
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
            helper.setUserAgent(userAgent);
            helper.setMethod("POST");
            helper.setContentType("application/json");

            helper.writeString(requestObj.toString());

            JsonObject responseObj = (JsonObject) helper.getResponseJson();

            return new Player(Utils.fromUndashed(responseObj.get("selectedProfile").getAsJsonObject().get("id").getAsString()), responseObj.get("selectedProfile").getAsJsonObject().get("name").getAsString(), responseObj.get("accessToken").getAsString());

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public Player refresh(String accessToken) throws MojangAPIException {
        try {
            APIHelper helper = new APIHelper(new URL(BASE_PATH + "/refresh"));

            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("accessToken", accessToken);
            requestObj.addProperty("clientToken", this.clientToken);
            requestObj.addProperty("requestUser", true);

            // Set connection flags
            helper.setUserAgent(userAgent);
            helper.setMethod("POST");
            helper.setContentType("application/json");

            helper.writeString(requestObj.toString());

            JsonObject responseObj = (JsonObject) helper.getResponseJson();

            return new Player(Utils.fromUndashed(responseObj.get("selectedProfile").getAsJsonObject().get("id").getAsString()), responseObj.get("selectedProfile").getAsJsonObject().get("name").getAsString(), responseObj.get("accessToken").getAsString());

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public boolean validate(String accessToken) throws MojangAPIException{
        try {
            APIHelper helper = new APIHelper(new URL(BASE_PATH + "/validate"));

            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("accessToken", accessToken);
            requestObj.addProperty("clientToken", this.clientToken);

            // Set connection flags
            helper.setUserAgent(userAgent);
            helper.setMethod("POST");
            helper.setContentType("application/json");

            helper.writeString(requestObj.getAsString());

            return helper.getResponseCode() >= 200 && helper.getResponseCode() <= 299;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public boolean signout(String username, String password) throws MojangAPIException{
        try {
            APIHelper helper = new APIHelper(new URL(BASE_PATH + "/signout"));
            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("username", username);
            requestObj.addProperty("password", password);

            // Set connection flags
            helper.setUserAgent(userAgent);
            helper.setMethod("POST");
            helper.setContentType("application/json");

            helper.writeString(requestObj.toString());

            return helper.getResponseCode() >= 200 && helper.getResponseCode() <= 299;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }

    public boolean invalidate(String accessToken) throws MojangAPIException {
        try {
            APIHelper helper = new APIHelper(new URL(BASE_PATH + "/invalidate"));
            JsonObject requestObj = new JsonObject();
            requestObj.addProperty("accessToken", accessToken);
            requestObj.addProperty("clientToken", this.clientToken);

            // Set connection flags
            helper.setUserAgent(userAgent);
            helper.setMethod("POST");
            helper.setContentType("application/json");

            helper.writeString(requestObj.toString());

            return helper.getResponseCode() >= 200 && helper.getResponseCode() <= 299;

        } catch (IOException ex) {
            throw new MojangAPIException(ex.getMessage());
        }
    }


}
