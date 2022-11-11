package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.spigot.managers.games.GameType;
import fr.multimc.api.spigot.managers.games.instances.InstanceSettings;
import fr.multimc.api.spigot.managers.games.instances.InstancesManager;
import fr.multimc.api.spigot.managers.teams.TeamManager;
import fr.multimc.api.spigot.samplecode.SampleCode;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.schematics.Schematic;
import fr.multimc.api.spigot.tools.schematics.SchematicOptions;
import fr.multimc.api.spigot.tools.utils.messages.MessagesFactory;
import fr.multimc.api.spigot.tools.worlds.MmcWorld;
import fr.multimc.api.spigot.tools.worlds.WorldSettings;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class TeamSampleCode implements SampleCode, Listener {

    private InstancesManager instancesManager;
    private TeamManager teamManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run(JavaPlugin plugin) {
        new File(plugin.getDataFolder().getPath() + "/database.db").delete();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        teamManager = new TeamManager(database);
        teamManager.addTeam("T1", "Name 1", "Xen0Xys");
        teamManager.addTeam("T2", "Name 2", "XenAdmin");

        MessagesFactory factory = new MessagesFactory("&eTeamSample");

        Schematic schematic = new Schematic(plugin, "instances_test");
        InstanceSettings settings = new InstanceSettings(schematic,
                new SchematicOptions(),
                GameType.SOLO,
                120, List.of(new RelativeLocation[]{new RelativeLocation(-2.5, 1, -1.5), new RelativeLocation(-4.5, 4, -5.5)}),
                null,
                null,
                20);
        WorldSettings lobbyWorldSettings = new WorldSettings("multimc_lobby",
                schematic,
                true);
        lobbyWorldSettings.setDifficulty(Difficulty.PEACEFUL);
        lobbyWorldSettings.setGameMode(GameMode.ADVENTURE);
        WorldSettings gameWorldSettings = new WorldSettings("multimc_game",
                null,
                true);
        gameWorldSettings.setDifficulty(Difficulty.PEACEFUL);
        gameWorldSettings.setPreventDamages(false);
        instancesManager = new InstancesManager(plugin, CustomInstanceSample.class, settings, factory, new MmcWorld(plugin, lobbyWorldSettings), new MmcWorld(plugin, gameWorldSettings));
        instancesManager.preAllocate(32);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent e){
        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        if(message.contains("start")){
            e.setCancelled(true);
            // Temp
//            List<MmcTeam> teams = new ArrayList<>();
//            MmcTeam team = new MmcTeam("T1", "CODELA", new MmcPlayer("Xen0Xys"));
//            for(int i = 0; i < 32; i++){
//                teams.add(team);
//            }
//            instancesManager.start(teams);
            instancesManager.start(teamManager.loadTeams());
        }else if(message.contains("stop")){
            e.setCancelled(true);
            instancesManager.stopManager();
        }else if(message.contains("push")){
            HashMap<String, Integer> scores = new HashMap<>();
            scores.put("T1", 16);
            scores.put("T2", 15);
            teamManager.pushScores(scores);
        }
    }
}
