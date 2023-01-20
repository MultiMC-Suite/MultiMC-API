package fr.multimc.api.spigot.pre_made.commands.executors;

import fr.multimc.api.spigot.managers.GamesManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DebugCommand implements CommandExecutor {

    private final GamesManager gamesManager;

    public DebugCommand(GamesManager gamesManager){
        this.gamesManager = gamesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player)
            player.teleport(new Location(gamesManager.getGameWorld().getWorld(), 0, 100, 0));
        return false;
    }
}
