package fr.multimc.api.commons.tools.times;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

public enum TimeUnit {
    YEAR("YYYY", "Year", 31536000000L),
    MONTH("MM", "Month", 2592000000L),
    DAY("DD", "Day", 86400000L),
    HOUR("hh", "Hour", 3600000L),
    MINUTE("mm", "Minute", 60000L),
    SECOND("ss", "Second", 1000L),
    MILLISECOND("ms", "Millisecond", 1L);

    private final String shortcut, name;
    private final long duration;

    TimeUnit(String shortcut, String name, long duration) {
        this.shortcut = shortcut;
        this.name = name;
        this.duration = duration;
    }

    public static Optional<TimeUnit> fromShortcut(@Nonnull String shortcut) {
        return Arrays.stream(values()).filter(unit -> unit.getShortcut().equals(shortcut)).findFirst();
    }

    public static Optional<TimeUnit> fromName(@Nonnull String name) {
        return Arrays.stream(values()).filter(unit -> unit.getName().equals(name)).findFirst();
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }
}
