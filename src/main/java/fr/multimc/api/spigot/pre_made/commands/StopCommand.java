package fr.multimc.api.spigot.pre_made.commands;

import fr.multimc.api.spigot.managers.instance.InstancesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class StopCommand implements CommandExecutor {

    private final InstancesManager instancesManager;

    public StopCommand(@NotNull InstancesManager instancesManager) {
        this.instancesManager = instancesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Stopping instances...");
        this.instancesManager.stopManager();
        commandSender.sendMessage("Instances stopped!");
        return true;
    }
}
