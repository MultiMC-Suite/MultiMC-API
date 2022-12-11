package fr.multimc.api.spigot.pre_made.samplecode.instances;

import fr.multimc.api.spigot.managers.games.GameInstance;
import fr.multimc.api.spigot.managers.games.settings.GameSettings;
import fr.multimc.api.spigot.managers.games.GamesManager;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomGameInstanceSample extends GameInstance {

    public CustomGameInstanceSample(JavaPlugin plugin, GamesManager gamesManager, GameSettings settings, Location instanceLocation, List<MmcTeam> mmcTeams, int instanceId) {
        super(plugin, gamesManager, settings, instanceLocation, mmcTeams, instanceId);
    }

    @Override
    public void tick() {

    }
}
