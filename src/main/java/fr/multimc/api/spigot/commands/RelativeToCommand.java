package fr.multimc.api.spigot.commands;

import fr.multimc.api.spigot.tools.chat.ClickableMessageBuilder;
import fr.multimc.api.spigot.tools.chat.TextBuilder;
import net.kyori.adventure.text.event.ClickEvent;
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

                commandSender.sendMessage(
                        new ClickableMessageBuilder(String.format("Relative location: %.1f ; %.1f ; %.1f", x, y, z))
                                .addExtras(
                                        "\n\n",
                                        new ClickableMessageBuilder("&bConfiguration")
                                                .setClick(ClickEvent.copyToClipboard(String.format("%s:%s:%s", formatter.format(x), formatter.format(y), formatter.format(z))))
                                                .setHover(new TextBuilder("&eCopy configuration\nto clipboard").build()),
                                        "&6   ",
                                        new ClickableMessageBuilder("&cCode")
                                                .setClick(ClickEvent.copyToClipboard(String.format("new RelativeLocation(%s, %s, %s)", formatter.format(x), formatter.format(y), formatter.format(z))))
                                                .setHover(new TextBuilder("&eCopy code\nto clipboard").build())
                                )
                                .build()
                );
            }
        }
        return true;
    }
}
