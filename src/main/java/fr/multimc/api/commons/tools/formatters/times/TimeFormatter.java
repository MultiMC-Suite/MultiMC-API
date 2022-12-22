package fr.multimc.api.commons.tools.formatters.times;

import fr.multimc.api.commons.tools.formatters.times.enums.TimeUnit;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class TimeFormatter {
    public static String format(long time, @Nonnull String format) {
        long timeFormat = time;
        Map<TimeUnit, Integer> mapped = new HashMap<>();

        for (TimeUnit unit : TimeUnit.values()) {
            mapped.put(unit, 0);

            while (timeFormat >= unit.getDuration()) {
                timeFormat -= unit.getDuration();
                mapped.replace(unit, mapped.get(unit) + 1);
            }

            int unitValue = mapped.get(unit);
            format = format.replaceAll(unit.getShortcut(), (unitValue < 10 ? "0" : "") + unitValue);
        }

        return format;
    }

    public static String formatGap(long start, long end, @Nonnull String format) {
        return format(start - end, format);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static String now(@Nonnull String format) {
        return format(now(), format);
    }
}
