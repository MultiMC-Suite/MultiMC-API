package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.managers.game.GameType;
import fr.multimc.api.commons.managers.game.instances.InstanceManager;
import fr.multimc.api.commons.managers.game.instances.InstanceSettings;
import fr.multimc.api.commons.managers.teammanager.TeamManager;
import fr.multimc.api.spigot.samplecode.SampleCode;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class TeamSampleCode implements SampleCode, Listener {

    private TeamManager teamManager;
    private InstanceManager instanceManager;
    private World lobbyWorld;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run(JavaPlugin plugin) {
        new File(plugin.getDataFolder().getPath() + "/database.db").delete();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        teamManager = new TeamManager(database);
        teamManager.addTeam("T1", "Xen0Xys");

        File schemFile = new File(plugin.getDataFolder().getPath() + "/schematics/instances_test.schem");
        InstanceSettings settings = new InstanceSettings(schemFile,
                GameType.ONLY_TEAM,
                600, new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>(),
                20,
                "multimc");
        instanceManager = new InstanceManager(plugin, GameType.SOLO, CustomInstanceSample.class, settings);
        lobbyWorld = instanceManager.getLobbyWorld();

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Location tpLocation = new Location(lobbyWorld, 0.5, 100, 0.5);
        e.getPlayer().teleport(tpLocation);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent e){
        System.out.println(e.message().toString());
        if(e.message().toString().equals("start")){
            teamManager.loadTeams();
            try {
                instanceManager.start(teamManager.getTeams());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
