package fr.multimc.api.spigot.tools.players;

import java.util.Arrays;
import java.util.Optional;

public enum PlayerSpeed {
    LEVEL_0(0f, 0f),
    LEVEL_1(.1f, .2f),
    LEVEL_2(.2f, .25f),
    LEVEL_3(.3f, .3f),
    LEVEL_4(.4f, .4f),
    LEVEL_5(.5f, .5f),
    LEVEL_6(.6f, .6f),
    LEVEL_7(.7f, .7f),
    LEVEL_8(.8f, .8f),
    LEVEL_9(.9f, .9f),
    LEVEL_10(1f, 1f);

    private final float walkLevel, flyLevel;

    PlayerSpeed(float walkLevel, float flyLevel) {
        this.walkLevel = walkLevel;
        this.flyLevel = flyLevel;
    }

    /**
     *
     * @param walkSpeed
     * @return
     */
    public static Optional<PlayerSpeed> fromWalkSpeed(float walkSpeed) {
        return Arrays.stream(values()).filter(speed -> speed.getWalkLevel() == walkSpeed).findFirst();
    }

    /**
     *
     * @param flySpeed
     * @return
     */
    public static Optional<PlayerSpeed> fromFlySpeed(float flySpeed) {
        return Arrays.stream(values()).filter(speed -> speed.getFlyLevel() == flySpeed).findFirst();
    }

    /**
     *
     * @return
     */
    public float getWalkLevel() {
        return walkLevel;
    }

    /**
     *
     * @return
     */
    public float getFlyLevel() {
        return flyLevel;
    }
}
