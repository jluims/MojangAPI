package net.brxen.mojangapi;

import java.util.Locale;

public enum SkinType {

    CLASSIC,
    SLIM;

    public String asString() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
