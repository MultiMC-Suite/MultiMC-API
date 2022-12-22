package fr.multimc.api.spigot.pre_made.samplecode.messages;

import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import fr.multimc.api.commons.tools.messages.enums.MessageType;
import fr.multimc.api.commons.tools.messages.MessagesFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageSampleCode implements SampleCode, Listener {

    private MessagesFactory factory;

    @Override
    public void run(JavaPlugin plugin) {
        this.factory = new MessagesFactory("&ePluginSample");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.sendMessage(factory.getChatMessage(MessageType.PREFIXED, player.getName(), "Sample &nchat&f message", null));
        player.sendMessage(factory.getChatMessage(MessageType.GAME, player.getName(), "Sample &ngame&f message", "TeamName"));
        player.sendMessage(factory.getChatMessage(MessageType.TEAM, player.getName(), "Sample &nwhisper&f message", null));
    }

}
