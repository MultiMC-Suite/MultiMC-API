package fr.multimc.api.spigot.tools.utils.strings;

import fr.multimc.api.spigot.tools.utils.random.MmcRandom;
import fr.multimc.api.spigot.tools.utils.strings.enums.StringConstraint;

import java.util.List;

@SuppressWarnings("unused")
public class StringGenerator {
    public static String generate(int length, StringConstraint... constraints) {
        List<StringConstraint> constraintsList = List.of(constraints);
        StringBuilder constraintsBuilder = new StringBuilder();
        if (constraintsList.contains(StringConstraint.LOWER)) constraintsBuilder.append(StringConstraint.LOWER.getValue());
        if (constraintsList.contains(StringConstraint.UPPER)) constraintsBuilder.append(StringConstraint.UPPER.getValue());
        if (constraintsList.contains(StringConstraint.NUMBERS)) constraintsBuilder.append(StringConstraint.NUMBERS.getValue());
        if (constraintsList.contains(StringConstraint.SPECIALS)) constraintsBuilder.append(StringConstraint.SPECIALS.getValue());
        String constraintsString = constraintsBuilder.toString();

        StringBuilder result = new StringBuilder();
        MmcRandom random = new MmcRandom();
        for (int i = 0; i < length; i++) result.append(constraintsString.charAt(random.nextInt(constraintsString.length())));

        return result.toString();
    }
}
