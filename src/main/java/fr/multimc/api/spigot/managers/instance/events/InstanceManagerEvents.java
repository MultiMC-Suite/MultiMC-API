package fr.multimc.api.spigot.managers.instance.events;

import fr.multimc.api.commons.tools.messages.ComponentBuilder;
import fr.multimc.api.commons.tools.messages.enums.MessageType;
import fr.multimc.api.spigot.managers.instance.Instance;
import fr.multimc.api.spigot.managers.instance.InstancesManager;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class InstanceManagerEvents implements Listener {

    private final InstancesManager instancesManager;
    private final Logger logger;

    public InstanceManagerEvents(InstancesManager instancesManager, Logger logger){
        this.instancesManager = instancesManager;
        this.logger = logger;
    }

    /**
     *
     * @param e PlayerMoveEvent
     */
    @EventHandler
    public void onPLayerMove(PlayerMoveEvent e){
        if(!e.getPlayer().getWorld().equals(this.instancesManager.getGameWorld().getWorld())) return;
        if(this.instancesManager.isStarted()) return;
        if(e.getFrom().distance(e.getTo()) > 0) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        if(!this.instancesManager.isStarted()){
            this.logger.info(String.format("Instance manager not started, teleporting player %s to lobby", mmcPlayer.getName()));
            mmcPlayer.teleport(this.instancesManager.getLobbyWorld().getSpawnPoint());
            player.getInventory().clear();
            this.logger.info(String.format("Player %s teleported to lobby...", mmcPlayer.getName()));
            return;
        }
        for(Instance instance : this.instancesManager.getInstances()){
            if(!instance.isPlayerOnInstance(mmcPlayer)) continue;
            if(instance.isRunning()){
                this.logger.info(String.format("Reconnecting player %s to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
                instance.onPlayerReconnect(mmcPlayer);
                this.logger.info(String.format("Player %s reconnected to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
            }else{
                this.logger.info(String.format("Instance %d not running, teleporting player %s to lobby...", instance.getInstanceId(), mmcPlayer.getName()));
                mmcPlayer.teleport(this.instancesManager.getLobbyWorld().getSpawnPoint());
                this.logger.info(String.format("Player %s teleported to lobby...", mmcPlayer.getName()));
                player.getInventory().clear();
            }
            return;
        }
        this.logger.info(String.format("No instance found for player %s, make him a spectator", mmcPlayer.getName()));
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e){
        MmcPlayer mmcPlayer = new MmcPlayer(e.getPlayer());
        if(!this.instancesManager.isStarted()) return;
        for(Instance instance : this.instancesManager.getInstances()){
            if(!instance.isPlayerOnInstance(mmcPlayer) || !instance.isRunning()) continue;
            this.logger.info(String.format("Disconnecting player %s to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
            instance.onPlayerDisconnect(mmcPlayer);
            this.logger.info(String.format("Player %s disconnected to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(!e.getPlayer().getWorld().equals(this.instancesManager.getGameWorld().getWorld())) return;
        if(!this.instancesManager.isStarted()) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(!e.getEntity().getWorld().equals(this.instancesManager.getGameWorld().getWorld())) return;
        if(!this.instancesManager.isStarted()) e.setCancelled(true);
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e){
        if(this.instancesManager.getMessageFactory() == null) return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        Component newMessage;
        if(!this.instancesManager.isStarted()){
            newMessage = this.instancesManager.getMessageFactory().getChatMessage(MessageType.PREFIXED, Component.text(player.getName()), e.message(), null);
            for(Player _player: Bukkit.getOnlinePlayers()){
                _player.sendMessage(newMessage);
            }
            return;
        }
        for(Instance instance: this.instancesManager.getInstances()){
            if(!instance.isPlayerOnInstance(new MmcPlayer(player))) continue;
            MmcTeam team = this.instancesManager.getTeamFromPlayer(mmcPlayer);
            switch (this.instancesManager.getSettings().gameType()) {
                case SOLO, ONLY_TEAM -> {
                    // Send message to player's team
                    if (team == null) return;
                    newMessage = this.instancesManager.getMessageFactory().getChatMessage(MessageType.TEAM, Component.text(player.getName()), e.message(), Component.text(team.getName()));
                    team.sendMessage(newMessage);
                }
                case TEAM_VS_TEAM -> {
                    String rawMessage = PlainTextComponentSerializer.plainText().serialize(e.message());
                    String chatPrefix = MessageType.GAME.getChatPrefix();
                    if (chatPrefix == null || team == null) return;
                    if (rawMessage.startsWith(chatPrefix)) {
                        // Send message to instance players
                        String message = rawMessage.replaceFirst(chatPrefix, "");
                        newMessage = this.instancesManager.getMessageFactory().getChatMessage(MessageType.GAME, Component.text(player.getName()), new ComponentBuilder(message).build(), Component.text(team.getName()));
                        for (MmcTeam instanceTeam : instance.getTeams()) {
                            instanceTeam.sendMessage(newMessage);
                        }
                    } else {
                        // Send message to player's team
                        newMessage = this.instancesManager.getMessageFactory().getChatMessage(MessageType.TEAM, Component.text(player.getName()), e.message(), Component.text(team.getName()));
                        team.sendMessage(newMessage);
                    }
                }
                default -> player.sendMessage(Component.text("Game type not supported").color(NamedTextColor.RED));
            }
            return;
        }
    }

}
