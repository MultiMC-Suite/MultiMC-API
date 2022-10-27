package fr.multimc.api.commons.tools.compares;

@SuppressWarnings("unused")
public class MathNumber {
    public static boolean isDoubleBetween(double number, double min, double max) {
        return number >= min && number <= max;
    }

    public static boolean isIntBetween(int number, int min, int max) {
        return number >= min && number <= max;
    }

    public static boolean isLongBetween(long number, long min, long max) {
        return number >= min && number <= max;
    }
}
