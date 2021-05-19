package net.brxen.mojangapi;

public enum Status {

    GREEN,
    YELLOW,
    RED;

    public static Status fromString(String s) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(s)) {
                return status;
            }
        }
        return null;
    }

}
