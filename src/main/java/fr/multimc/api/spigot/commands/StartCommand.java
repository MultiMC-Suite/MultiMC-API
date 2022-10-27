package fr.multimc.api.spigot.commands;

import fr.multimc.api.spigot.managers.games.instances.InstancesManager;
import fr.multimc.api.spigot.managers.teams.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class StartCommand implements CommandExecutor {
    private final InstancesManager instancesManager;
    private final TeamManager teamManager;

    public StartCommand(InstancesManager instancesManager, TeamManager teamManager){
        this.instancesManager = instancesManager;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Starting instances...");
        this.instancesManager.start(this.teamManager.loadTeams());
        commandSender.sendMessage("Instances started!");
        return true;
    }
}
