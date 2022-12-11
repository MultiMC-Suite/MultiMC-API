package fr.multimc.api.spigot.pre_made.samplecode.instances;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.spigot.managers.games.enums.GameType;
import fr.multimc.api.spigot.tools.settings.InstanceSettings;
import fr.multimc.api.spigot.managers.games.GamesManager;
import fr.multimc.api.spigot.managers.teams.TeamManager;
import fr.multimc.api.spigot.pre_made.commands.completers.StartTabCompleter;
import fr.multimc.api.spigot.pre_made.commands.executors.StartCommand;
import fr.multimc.api.spigot.pre_made.commands.executors.StopCommand;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import fr.multimc.api.spigot.tools.settings.enums.WorldPrevention;
import fr.multimc.api.spigot.tools.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.worlds.schematics.Schematic;
import fr.multimc.api.spigot.tools.worlds.schematics.SchematicOptions;
import fr.multimc.api.commons.tools.messages.MessagesFactory;
import fr.multimc.api.spigot.tools.worlds.MmcWorld;
import fr.multimc.api.spigot.tools.settings.WorldSettings;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "ConstantConditions"})
public class InstanceSampleCode implements SampleCode, Listener {

    private GamesManager gamesManager;
    private TeamManager teamManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run(JavaPlugin plugin) {
        new File(plugin.getDataFolder().getPath() + "/database.db").delete();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        teamManager = new TeamManager(database);
        teamManager.addTeam("T1", "Name 1", "Xen0Xys");
//        teamManager.addTeam("T2", "Name 2", "XenAdmin");

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
                GameMode.ADVENTURE);
        lobbyWorldSettings.addPrevention(WorldPrevention.ALL);
        WorldSettings gameWorldSettings = new WorldSettings(
                "multimc_game");
        gameWorldSettings.addPrevention(WorldPrevention.ALL);
        gameWorldSettings.addPrevention(WorldPrevention.PREVENT_DAMAGES); // Withdraw prevention because Prevention.ALL is already present
        gameWorldSettings.addGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        gameWorldSettings.addGameRule(GameRule.DO_WEATHER_CYCLE, false);
        gameWorldSettings.setDifficulty(Difficulty.PEACEFUL);
        gamesManager = new GamesManager(plugin, CustomGameInstanceSample.class, settings, factory, new MmcWorld(plugin, lobbyWorldSettings), new MmcWorld(plugin, gameWorldSettings));
        gamesManager.preAllocate(5);

        // Commands
        plugin.getCommand("stop-mmc").setExecutor(new StopCommand(gamesManager));
        plugin.getCommand("start-mmc").setExecutor(new StartCommand(gamesManager, teamManager));
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
            gamesManager.start(e.getPlayer(), teamManager.loadTeams());
        }else if(message.contains("stop")){
            e.setCancelled(true);
            gamesManager.stopManager();
        }else if(message.contains("push")){
            HashMap<String, Integer> scores = new HashMap<>();
            scores.put("T1", 16);
//            scores.put("T2", 15);
            teamManager.pushScores(scores);
        }
    }
}
