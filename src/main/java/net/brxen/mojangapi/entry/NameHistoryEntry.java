package net.brxen.mojangapi.entry;

public class NameHistoryEntry {

    private final String name;
    private Long date;

    public NameHistoryEntry(String name, Long date) {
        this.name = name;
        this.date = date;
    }

    public NameHistoryEntry(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Long getDate() {
        return date;
    }
}
