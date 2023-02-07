package fr.multimc.api.sample.spigot.gui;

import fr.multimc.api.sample.spigot.SampleCode;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public class GuiSampleCode implements SampleCode, CommandExecutor {

    private SampleGui sampleGui;

    @Override
    public void run(JavaPlugin plugin) {
        // Create a new gui
        this.sampleGui = new SampleGui(plugin);
        // Register debug command
        plugin.getCommand("debug-mmc").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player)
            this.sampleGui.openInventory(new MmcPlayer(player));
        return false;
    }
}
