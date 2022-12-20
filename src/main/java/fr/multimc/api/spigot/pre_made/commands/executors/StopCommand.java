package fr.multimc.api.spigot.pre_made.commands.executors;

import fr.multimc.api.spigot.managers.GamesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class StopCommand implements CommandExecutor {

    private final GamesManager gamesManager;

    public StopCommand(@NotNull GamesManager gamesManager) {
        this.gamesManager = gamesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Stopping instances...");
        this.gamesManager.stopManager();
        commandSender.sendMessage("Instances stopped!");
        return true;
    }
}
