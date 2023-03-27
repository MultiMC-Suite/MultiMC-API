package fr.multimc.api.spigot.common.commands.executors;

import fr.multimc.api.spigot.games.GamesManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that implements the {@link CommandExecutor} interface for the "debug" command.
 * Allows for teleporting a player to a specific location in the game world.
 */
public class DebugCommand implements CommandExecutor {

    private final GamesManager gamesManager;

    /**
     * Constructor for the DebugCommand class.
     * @param gamesManager The {@link GamesManager} instance to be used for getting the game world.
     */
    public DebugCommand(GamesManager gamesManager){
        this.gamesManager = gamesManager;
    }

    /**
     * Executes the "debug" command when called.
     * @param commandSender The {@link CommandSender} of the command.
     * @param command The {@link Command} being executed.
     * @param s {@link String} that represents yhe name of the command being executed.
     * @param strings {@link String}[] that represents the additional arguments passed with the command.
     * @return boolean indicating if the command was successful or not.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player)
            player.teleport(new Location(gamesManager.getGameWorld().getWorld(), 0, 100, 0));
        return false;
    }
}
