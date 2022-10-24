package fr.multimc.api.spigot.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class RelativeToCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player){
            if(strings.length >= 3){
                Location playerLocation = player.getLocation();
                double x = playerLocation.getX() - Double.parseDouble(strings[0]);
                double y = playerLocation.getY() - Double.parseDouble(strings[1]);
                double z = playerLocation.getZ() - Double.parseDouble(strings[2]);
                DecimalFormat formatter = new DecimalFormat();
                formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
                Component message = Component
                        .text(String.format("Relative location: %.1f %.1f %.1f", x, y, z)).color(TextColor.color(0x00FF00))
                        .clickEvent(ClickEvent.copyToClipboard(String.format("new RelativeLocation(%s, %s, %s)",
                                formatter.format(x),
                                formatter.format(y),
                                formatter.format(z))))
                        .hoverEvent(Component.text("Click to copy code").color(TextColor.color(0xFF8000)));
                commandSender.sendMessage(message);
            }
        }
        return true;
    }
}
