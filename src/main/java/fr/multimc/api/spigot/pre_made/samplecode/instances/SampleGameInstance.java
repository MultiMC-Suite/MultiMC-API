package fr.multimc.api.spigot.pre_made.samplecode.instances;

import fr.multimc.api.spigot.games.GameInstance;
import fr.multimc.api.spigot.games.settings.GameSettings;
import fr.multimc.api.spigot.managers.GamesManager;
import fr.multimc.api.spigot.teams.MmcTeam;
import fr.multimc.api.spigot.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.worlds.locations.zones.effects.ZoneEffect;
import fr.multimc.api.spigot.worlds.locations.zones.effects.effects.PotionZoneEffect;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class SampleGameInstance extends GameInstance{

    private final ZoneEffect zoneEffect;

    public SampleGameInstance(JavaPlugin plugin, GamesManager gamesManager, GameSettings settings, Location instanceLocation, List<MmcTeam> mmcTeams, int instanceId) {
        super(plugin, gamesManager, settings, instanceLocation, mmcTeams, instanceId);

        PotionZoneEffect potionZoneEffect = new PotionZoneEffect(PotionEffectType.REGENERATION, 1, Integer.MAX_VALUE, false);
        this.zoneEffect = new ZoneEffect(plugin, instanceLocation, new RelativeLocation(2, 0, 2), new RelativeLocation(-11, 5, -8), potionZoneEffect);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {

    }
}
