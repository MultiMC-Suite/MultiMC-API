package fr.multimc.api.spigot.sample.spigot.instances;

import fr.multimc.api.commons.data.DataSourceLoader;
import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.tools.messages.MessagesFactory;
import fr.multimc.api.spigot.sample.spigot.SampleCode;
import fr.multimc.api.spigot.common.commands.completers.StartTabCompleter;
import fr.multimc.api.spigot.common.commands.executors.StartCommand;
import fr.multimc.api.spigot.common.commands.executors.StopCommand;
import fr.multimc.api.spigot.common.worlds.MmcWorld;
import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.common.worlds.schematics.Schematic;
import fr.multimc.api.spigot.common.worlds.schematics.SchematicOptions;
import fr.multimc.api.spigot.common.worlds.settings.WorldSettings;
import fr.multimc.api.spigot.common.worlds.settings.enums.GameRuleSet;
import fr.multimc.api.spigot.common.worlds.settings.enums.WorldPrevention;
import fr.multimc.api.spigot.games.GamesManager;
import fr.multimc.api.spigot.games.enums.GameType;
import fr.multimc.api.spigot.games.settings.GameSettings;
import fr.multimc.api.spigot.games.settings.GamesManagerSettings;
import fr.multimc.api.spigot.games.teams.TeamManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
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
    private static ScoreboardLibrary scoreboardLibrary;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run(JavaPlugin plugin) {
        // Load scoreboard library
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLibrary = new NoopScoreboardLibrary();
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.saveDefaultConfig();

        new File(plugin.getDataFolder().getPath() + "/database.db").delete();
        Database database = (Database) new DataSourceLoader(plugin.getConfig(), plugin.getDataFolder(), plugin.getLogger()).loadDataSource();
        teamManager = new TeamManager(database);
        teamManager.addTeam("T1", "Name 1", "Xen0Xys");
//        teamManager.addTeam("T2", "Name 2", "XenAdmin");

        MessagesFactory factory = new MessagesFactory(Component.text("InstanceSample").color(NamedTextColor.YELLOW));

        Schematic schematic = new Schematic(plugin, "instances_test", new SchematicOptions());
        GameSettings settings = new GameSettings(
                schematic,
                GameType.SOLO,
                null,
                List.of(new RelativeLocation[]{new RelativeLocation(-2.5, 1, -1.5), new RelativeLocation(-4.5, 4, -5.5)}),
                null,
                null,
                30,
                1,
                20);
        WorldSettings lobbyWorldSettings = new WorldSettings(
                "multimc_lobby",
                schematic,
                null,
                Difficulty.PEACEFUL,
                GameMode.ADVENTURE);
        lobbyWorldSettings
                .addGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
                .addGameRule(GameRule.DO_WEATHER_CYCLE, false);
        lobbyWorldSettings.addPrevention(WorldPrevention.ALL);
        WorldSettings gameWorldSettings = new WorldSettings(
                "multimc_game");
        gameWorldSettings
                .addPrevention(WorldPrevention.ALL)
                .addPrevention(WorldPrevention.PREVENT_DAMAGES); // Withdraw damage prevention because Prevention.ALL is already present
        gameWorldSettings.addGameRuleSet(GameRuleSet.DEFAULT);
        gameWorldSettings.setDifficulty(Difficulty.PEACEFUL);
        GamesManagerSettings gamesManagerSettings = new GamesManagerSettings(
                settings,
                SampleGameInstance.class,
                new MmcWorld(plugin, lobbyWorldSettings),
                new MmcWorld(plugin, gameWorldSettings),
                factory);
        gamesManager = new GamesManager(plugin, gamesManagerSettings);
        gamesManager.preAllocate(5);

        // Commands
//        plugin.getCommand("debug-mmc").setExecutor(new DebugCommand(gamesManager));
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

    public static ScoreboardLibrary getScoreboardLibrary() {
        return scoreboardLibrary;
    }
}
