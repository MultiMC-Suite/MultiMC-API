package fr.multimc.api.sample.spigot.gui;

import fr.multimc.api.sample.spigot.SampleCode;
import fr.multimc.api.sample.spigot.gui.basic.SampleGui;
import fr.multimc.api.sample.spigot.gui.linked.SampleLinkedGui;
import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.gui.GuiView;
import fr.multimc.api.spigot.common.gui.enums.GuiSize;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DataFlowIssue", "FieldCanBeLocal", "unused"})
public class GuiSampleCode implements SampleCode, CommandExecutor {

    private SampleGui sampleGui;
    private SampleLinkedGui sampleLinkedGui;

    @Override
    public void run(@NotNull final JavaPlugin plugin) {
        // Create a new gui
        this.sampleGui = new SampleGui(plugin);
        this.sampleLinkedGui = new SampleLinkedGui(plugin, new GuiView(GuiSize.DOUBLE_CHEST));
        // Register debug command
        plugin.getCommand("debug-mmc").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] strings) {
        if(commandSender instanceof Player player)
            this.sampleGui.openInventory(new MmcPlayer(player));
//            this.sampleGui.openInventory(new MmcPlayer(player));
        return false;
    }
}
