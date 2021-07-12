package net.brxen.mojangapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class APIHelper {

    private HttpURLConnection connection;

    public APIHelper(URL url) {
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void writeString(String str) throws IOException {
        connection.setDoOutput(true);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(str);
        writer.close();
    }

    public void setUserAgent(String userAgent) {
        connection.setRequestProperty("User-Agent", userAgent);
    }

    public void setMethod(String method) throws ProtocolException {
        connection.setRequestMethod(method);
    }

    public void setContentType(String contentType) {
        connection.setRequestProperty("Content-Type", contentType);
    }

    public void setAuthorization(String authorization) {
        connection.addRequestProperty("Authorization", authorization);
    }

    public int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    public String getResponseText() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder data = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            data.append(line);
        }

        reader.close();

        return data.toString();
    }

    public Object getResponseJson() throws IOException {
        String text = getResponseText();
        return new JsonParser().parse(text);
    }

}
