package net.brxen.mojangapi;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class Utils {

    public static UUID fromUndashed(String uuid) {
        return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    public static URL escapeURL(URL url) {
        try {
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readInputStream(InputStream s) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(s));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }

    public static void writeOutputStream(OutputStream stream, String toWrite) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

        writer.write(toWrite);
        writer.flush();
    }


    public static String toUndashed(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
