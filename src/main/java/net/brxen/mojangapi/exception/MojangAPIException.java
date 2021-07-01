package net.brxen.mojangapi.exception;

public class MojangAPIException extends Exception{

    public MojangAPIException(String message) {
        super("Mojang API request failed: " + message);
    }

}
