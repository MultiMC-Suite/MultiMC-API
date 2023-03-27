package fr.multimc.api.sample.spigot.instances;

import fr.multimc.api.spigot.common.scoreboards.MmcSidebar;
import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.common.worlds.locations.zones.effects.ZoneEffect;
import fr.multimc.api.spigot.common.worlds.locations.zones.effects.effects.PotionZoneEffect;
import fr.multimc.api.spigot.games.GameInstance;
import fr.multimc.api.spigot.games.GamesManager;
import fr.multimc.api.spigot.games.settings.GameSettings;
import fr.multimc.api.spigot.games.teams.MmcTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class SampleGameInstance extends GameInstance{

    private final ZoneEffect zoneEffect;
    private final MmcSidebar sidebar;

    public SampleGameInstance(JavaPlugin plugin, GamesManager gamesManager, GameSettings settings, Location instanceLocation, List<MmcTeam> mmcTeams, int instanceId) {
        super(plugin, gamesManager, settings, instanceLocation, mmcTeams, instanceId);

        PotionZoneEffect potionZoneEffect = new PotionZoneEffect(PotionEffectType.REGENERATION, 1, Integer.MAX_VALUE, false);
        this.zoneEffect = new ZoneEffect(plugin, instanceLocation, new RelativeLocation(2, 0, 2), new RelativeLocation(-11, 5, -8), potionZoneEffect);

        // Scoreboard sample
        this.sidebar = new MmcSidebar(InstanceSampleCode.getScoreboardLibrary(), 5);
        this.sidebar.getSidebar().title(Component.text("Sidebar title", NamedTextColor.GREEN));
        this.sidebar.getSidebar().line(0, Component.text("Test"));

    }

    @Override
    public void start() {
        super.start();
        this.getPlayers().forEach(this.sidebar::addPlayer);
    }

    @Override
    public void tick() {

    }
}
