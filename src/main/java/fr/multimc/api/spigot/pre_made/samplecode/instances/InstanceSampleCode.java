package fr.multimc.api.spigot.pre_made.samplecode.instances;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.spigot.managers.instance.enums.GameType;
import fr.multimc.api.spigot.tools.settings.InstanceSettings;
import fr.multimc.api.spigot.managers.instance.InstancesManager;
import fr.multimc.api.spigot.managers.teams.TeamManager;
import fr.multimc.api.spigot.pre_made.commands.completers.StartTabCompleter;
import fr.multimc.api.spigot.pre_made.commands.executors.StartCommand;
import fr.multimc.api.spigot.pre_made.commands.executors.StopCommand;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import fr.multimc.api.spigot.tools.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.worlds.schematics.Schematic;
import fr.multimc.api.spigot.tools.worlds.schematics.SchematicOptions;
import fr.multimc.api.spigot.tools.messages.MessagesFactory;
import fr.multimc.api.spigot.tools.worlds.MmcWorld;
import fr.multimc.api.spigot.tools.settings.WorldSettings;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "ConstantConditions"})
public class InstanceSampleCode implements SampleCode, Listener {

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

        MessagesFactory factory = new MessagesFactory(Component.text("InstanceSample").color(NamedTextColor.YELLOW));

        Schematic schematic = new Schematic(plugin, "instances_test", new SchematicOptions());
        InstanceSettings settings = new InstanceSettings(
                schematic,
                GameType.SOLO,
                List.of(new RelativeLocation[]{new RelativeLocation(-2.5, 1, -1.5), new RelativeLocation(-4.5, 4, -5.5)}),
                null,
                null,
                120,
                1,
                20);
        WorldSettings lobbyWorldSettings = new WorldSettings(
                "multimc_lobby",
                schematic,
                null,
                Difficulty.PEACEFUL,
                true);
        lobbyWorldSettings.setGameMode(GameMode.ADVENTURE);
        WorldSettings gameWorldSettings = new WorldSettings(
                "multimc_game",
                true);
        gameWorldSettings.setDifficulty(Difficulty.PEACEFUL);
        gameWorldSettings.setPreventDamages(false);
        instancesManager = new InstancesManager(plugin, CustomInstanceSample.class, settings, factory, new MmcWorld(plugin, lobbyWorldSettings), new MmcWorld(plugin, gameWorldSettings));
        instancesManager.preAllocate(32);

        // Commands
        plugin.getCommand("stop-mmc").setExecutor(new StopCommand(instancesManager));
        plugin.getCommand("start-mmc").setExecutor(new StartCommand(instancesManager, teamManager));
        plugin.getCommand("start-mmc").setTabCompleter(new StartTabCompleter());
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
