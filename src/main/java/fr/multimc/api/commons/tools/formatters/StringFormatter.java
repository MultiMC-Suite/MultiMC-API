package fr.multimc.api.commons.tools.formatters;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class StringFormatter {
    /**
     * Capitalize each word of a sentence.
     * @param string: String to capitalize.
     * @return String capitalized.
     */
    public static String capitalize(@Nonnull String string) {
        if (string.length() == 0) return string;
        if (string.length() == 1) return string.toUpperCase();
        if (!string.contains(" ")) return initialCapitalize(string);

        StringBuilder result = new StringBuilder();
        Arrays.asList(string.split(" ")).forEach(word -> result.append(" ").append(initialCapitalize(word)));
        return result.substring(1);
    }

    /**
     * Capitalize each sentence of a string (initial char to upper).
     * @param string: String to capitalize.
     * @return String capitalized.
     */
    public static String initialCapitalize(@Nonnull String string) {
        if (string.length() == 0) return string;
        if (string.length() == 1) return string.toUpperCase();
        if (!string.contains(".")) return string.substring(0, 1).toUpperCase() + string.substring(1);

        StringBuilder result = new StringBuilder();
        Arrays.asList(string.split("\\.")).forEach(sentence -> result.append(" ").append(sentence.substring(0, 1).toUpperCase()).append(sentence.substring(1)));
        return result.substring(1);
    }

    public static String reduce(@Nonnull String[] string) {
        return reduce(string, ' ');
    }

    public static String reduce(@Nonnull String[] string, char separator) {
        return reduce(string, String.valueOf(separator));
    }

    public static String reduce(@Nonnull String[] string, @Nonnull String separator) {
        StringBuilder result = new StringBuilder();
        Arrays.asList(string).forEach(word -> result.append(separator).append(word));
        return result.substring(separator.length());
    }
}
