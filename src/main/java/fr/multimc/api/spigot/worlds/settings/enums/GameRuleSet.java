package fr.multimc.api.spigot.worlds.settings.enums;

import org.bukkit.GameRule;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public enum GameRuleSet {

    DEFAULT(Map.of(
            GameRule.DO_DAYLIGHT_CYCLE, true,
            GameRule.DO_WEATHER_CYCLE, false,
            GameRule.ANNOUNCE_ADVANCEMENTS, false,
            GameRule.DO_FIRE_TICK, false
    ));

    private final Map<GameRule<?>, Object> gameRules = new HashMap<>();

    <T> GameRuleSet(Map<GameRule<?>, T> gameRules){
        this.gameRules.putAll(gameRules);
    }

    public <T> Map<GameRule<?>, T> getGameRules() {
        Map<GameRule<?>, T> typedGameRules = new HashMap<>();
        this.gameRules.forEach((key, value) -> typedGameRules.put(key, (T) value));
        return typedGameRules;
    }
}
