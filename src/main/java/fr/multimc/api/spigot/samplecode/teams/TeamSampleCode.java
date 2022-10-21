package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.spigot.customs.CustomLocation;
import fr.multimc.api.spigot.managers.games.GameType;
import fr.multimc.api.spigot.managers.games.Lobby;
import fr.multimc.api.spigot.managers.games.instances.InstancesManager;
import fr.multimc.api.spigot.managers.games.instances.InstanceSettings;
import fr.multimc.api.spigot.managers.teams.TeamManager;
import fr.multimc.api.spigot.samplecode.SampleCode;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamSampleCode implements SampleCode, Listener {

    private TeamManager teamManager;
    private InstancesManager instancesManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run(JavaPlugin plugin) {
        new File(plugin.getDataFolder().getPath() + "/database.db").delete();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        teamManager = new TeamManager(database);
        teamManager.addTeam("CODELA", "T1", "Xen0Xys");

        File schemFile = new File(plugin.getDataFolder().getPath() + "/schematics/instances_test.schem");
        InstanceSettings settings = new InstanceSettings(schemFile,
                GameType.SOLO,
                60, List.of(new CustomLocation[]{new CustomLocation(-2.5, 1, -1.5), new CustomLocation(-4.5, 4, -5.5)}),
                new ArrayList<>(),
                new HashMap<>(),
                20,
                "multimc");
        Lobby lobby = new Lobby(schemFile, new CustomLocation(0, 5, 0), "multimc_lobby");
        instancesManager = new InstancesManager(plugin, CustomInstanceSample.class, settings, lobby);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent e){
        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        if(message.contains("start")){
            e.setCancelled(true);
            try {
                // Temp
//                List<Team> teams = new ArrayList<>();
//                Team team = new Team("T1", 0, Bukkit.getPlayer("Xen0Xys"));
//                for(int i = 0; i < 32; i++){
//                    teams.add(team);
//                }
//                instanceManager.start(teams);
                instancesManager.start(teamManager.loadTeams());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }else if(message.contains("stop")){
            e.setCancelled(true);
            instancesManager.stopInstances();
        }
    }
}
