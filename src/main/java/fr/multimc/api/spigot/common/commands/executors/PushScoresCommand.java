package fr.multimc.api.spigot.common.commands.executors;

import fr.multimc.api.spigot.games.score.ScoreManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PushScoresCommand implements CommandExecutor {

    private final ScoreManager scoreManager;
    private final Plugin plugin;

    public PushScoresCommand(ScoreManager scoreManager, Plugin plugin) {
        this.scoreManager = scoreManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        new BukkitRunnable() {
            @Override
            public void run() {
                commandSender.sendMessage("Pushing scores...");
                scoreManager.pushScore();
                commandSender.sendMessage("Scores pushed!");
            }
        }.runTaskAsynchronously(this.plugin);
        return false;
    }
}
