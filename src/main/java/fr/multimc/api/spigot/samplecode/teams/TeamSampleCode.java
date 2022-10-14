package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.managers.game.CustomLocation;
import fr.multimc.api.commons.managers.game.GameType;
import fr.multimc.api.commons.managers.game.instances.InstanceManager;
import fr.multimc.api.commons.managers.game.instances.InstanceSettings;
import fr.multimc.api.commons.managers.teammanager.TeamManager;
import fr.multimc.api.spigot.samplecode.SampleCode;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import java.util.List;

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
        teamManager.addTeam("T2", "XenAdmin");

        File schemFile = new File(plugin.getDataFolder().getPath() + "/schematics/instances_test.schem");
        InstanceSettings settings = new InstanceSettings(schemFile,
                GameType.TEAM_VS_TEAM,
                600, List.of(new CustomLocation[]{new CustomLocation(-2.5, 1, -1.5), new CustomLocation(-4.5, 4, -5.5)}),
                new ArrayList<>(),
                new HashMap<>(),
                20,
                "multimc");
        instanceManager = new InstanceManager(plugin, CustomInstanceSample.class, settings);
        lobbyWorld = instanceManager.getLobbyWorld();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Location tpLocation = new Location(lobbyWorld, 0.5, 105, 0.5);
        e.getPlayer().teleport(tpLocation);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent e){
        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        if(message.contains("start")){
            e.setCancelled(true);
            System.out.println("Loading instance...");
            try {
                instanceManager.start(teamManager.loadTeams());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
