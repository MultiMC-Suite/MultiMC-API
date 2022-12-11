package fr.multimc.api.spigot.managers.games.events;

import fr.multimc.api.commons.tools.messages.ComponentBuilder;
import fr.multimc.api.commons.tools.messages.enums.MessageType;
import fr.multimc.api.spigot.managers.enums.ManagerState;
import fr.multimc.api.spigot.managers.games.GameInstance;
import fr.multimc.api.spigot.managers.games.GamesManager;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.logging.Logger;

public class GamesManagerEvents implements Listener {

    private final GamesManager gamesManager;
    private final Logger logger;

    public GamesManagerEvents(GamesManager gamesManager, Logger logger){
        this.gamesManager = gamesManager;
        this.logger = logger;
    }

    /**
     * Event call when a player move on the server
     * Only used to freeze the player if:
     * - The player is in the game world
     * - The GamesManager is not started
     * - The player is not a spectator
     * @param e PlayerMoveEvent object
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(!e.getPlayer().getWorld().equals(this.gamesManager.getGameWorld().getWorld())) return;
        if(this.gamesManager.getState() == ManagerState.STARTED) return;
        if(this.gamesManager.isSpectator(new MmcPlayer(e.getPlayer()))) return;
        if(e.getFrom().distance(e.getTo()) > 0) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        if(this.gamesManager.getState() != ManagerState.STARTING && this.gamesManager.getState() != ManagerState.STARTED){
            this.logger.info(String.format("Instance manager not started, teleporting player %s to lobby", mmcPlayer.getName()));
            mmcPlayer.teleport(this.gamesManager.getLobbyWorld().getSpawnPoint());
            player.getInventory().clear();
            this.logger.info(String.format("Player %s teleported to lobby...", mmcPlayer.getName()));
            return;
        }
        for(GameInstance gameInstance : this.gamesManager.getInstances()){
            if(!gameInstance.isPlayerOnInstance(mmcPlayer)) continue;
            if(gameInstance.isRunning()){
                this.logger.info(String.format("Reconnecting player %s to instance %d...", mmcPlayer.getName(), gameInstance.getInstanceId()));
                gameInstance.onPlayerReconnect(mmcPlayer);
                this.logger.info(String.format("Player %s reconnected to instance %d...", mmcPlayer.getName(), gameInstance.getInstanceId()));
            }else{
                this.logger.info(String.format("Instance %d not running, teleporting player %s to lobby...", gameInstance.getInstanceId(), mmcPlayer.getName()));
                mmcPlayer.teleport(this.gamesManager.getLobbyWorld().getSpawnPoint());
                this.logger.info(String.format("Player %s teleported to lobby...", mmcPlayer.getName()));
                player.getInventory().clear();
            }
            return;
        }
        // If player spawn in lobby or game world, set him as a spectator
        if(e.getPlayer().getWorld().equals(this.gamesManager.getLobbyWorld().getWorld()) || e.getPlayer().getWorld().equals(this.gamesManager.getGameWorld().getWorld())){
            this.logger.info(String.format("No instance found for player %s, make him a spectator", mmcPlayer.getName()));
            this.gamesManager.addSpectator(mmcPlayer, 0);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e){
        MmcPlayer mmcPlayer = new MmcPlayer(e.getPlayer());
        if(this.gamesManager.getState() != ManagerState.STARTED) return;
        for(GameInstance gameInstance : this.gamesManager.getInstances()){
            if(!gameInstance.isPlayerOnInstance(mmcPlayer) || !gameInstance.isRunning()) continue;
            this.logger.info(String.format("Disconnecting player %s to instance %d...", mmcPlayer.getName(), gameInstance.getInstanceId()));
            gameInstance.onPlayerDisconnect(mmcPlayer);
            this.logger.info(String.format("Player %s disconnected to instance %d...", mmcPlayer.getName(), gameInstance.getInstanceId()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(!e.getPlayer().getWorld().equals(this.gamesManager.getGameWorld().getWorld())) return;
        if(this.gamesManager.getState() != ManagerState.STARTED) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(!e.getEntity().getWorld().equals(this.gamesManager.getGameWorld().getWorld())) return;
        if(this.gamesManager.getState() != ManagerState.STARTED) e.setCancelled(true);
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e){
        if(this.gamesManager.getMessageFactory() == null) return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        Component newMessage;
        if(this.gamesManager.getState() != ManagerState.STARTED){
            newMessage = this.gamesManager.getMessageFactory().getChatMessage(MessageType.PREFIXED, Component.text(player.getName()), e.message(), null);
            for(Player _player: Bukkit.getOnlinePlayers()){
                _player.sendMessage(newMessage);
            }
            return;
        }
        for(GameInstance gameInstance : this.gamesManager.getInstances()){
            if(!gameInstance.isPlayerOnInstance(new MmcPlayer(player))) continue;
            MmcTeam team = this.gamesManager.getTeamFromPlayer(mmcPlayer);
            switch (this.gamesManager.getSettings().gameType()) {
                case SOLO, ONLY_TEAM -> {
                    // Send message to player's team
                    if (team == null) return;
                    newMessage = this.gamesManager.getMessageFactory().getChatMessage(MessageType.TEAM, Component.text(player.getName()), e.message(), Component.text(team.getName()));
                    team.sendMessage(newMessage);
                }
                case TEAM_VS_TEAM -> {
                    String rawMessage = PlainTextComponentSerializer.plainText().serialize(e.message());
                    String chatPrefix = MessageType.GAME.getChatPrefix();
                    if (chatPrefix == null || team == null) return;
                    if (rawMessage.startsWith(chatPrefix)) {
                        // Send message to instance players
                        String message = rawMessage.replaceFirst(chatPrefix, "");
                        newMessage = this.gamesManager.getMessageFactory().getChatMessage(MessageType.GAME, Component.text(player.getName()), new ComponentBuilder(message).build(), Component.text(team.getName()));
                        for (MmcTeam instanceTeam : gameInstance.getTeams()) {
                            instanceTeam.sendMessage(newMessage);
                        }
                    } else {
                        // Send message to player's team
                        newMessage = this.gamesManager.getMessageFactory().getChatMessage(MessageType.TEAM, Component.text(player.getName()), e.message(), Component.text(team.getName()));
                        team.sendMessage(newMessage);
                    }
                }
                default -> player.sendMessage(Component.text("Game type not supported").color(NamedTextColor.RED));
            }
            return;
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE) return;
        Player player = e.getPlayer();
        Player targetPlayer = e.getTo().getWorld().getPlayers().get(0);
        for(Player worldPlayer : e.getTo().getWorld().getPlayers()){
            if(worldPlayer.getLocation().distance(e.getTo()) < targetPlayer.getLocation().distance(e.getTo())){
                targetPlayer = worldPlayer;
            }
        }
        // If player go to another manager, remove him from spectators and stop here
        if(!e.getTo().getWorld().equals(this.gamesManager.getGameWorld().getWorld())){
            this.gamesManager.getSpectators().remove(new MmcPlayer(e.getPlayer()));
            return;
        }
        // If player came from another manager, to this manager, set him as a manager's spectator
        if(!e.getFrom().getWorld().equals(this.gamesManager.getGameWorld().getWorld()) && !e.getFrom().getWorld().equals(this.gamesManager.getLobbyWorld().getWorld())){
            if(e.getTo().getWorld().equals(this.gamesManager.getGameWorld().getWorld()) || e.getTo().getWorld().equals(this.gamesManager.getLobbyWorld().getWorld())){
                this.logger.info(String.format("Player %s came from another manager, make him a spectator", e.getPlayer().getName()));
                this.gamesManager.addSpectator(new MmcPlayer(e.getPlayer()), 0);
            }
        }

        // If target player is a spectator, set him spectate the same instance
        if(gamesManager.isSpectator(new MmcPlayer(targetPlayer))){
            int targetGameInstanceId = gamesManager.getSpectators().get(new MmcPlayer(targetPlayer));
            gamesManager.getSpectators().replace(new MmcPlayer(player), targetGameInstanceId);
            System.out.println("Player " + player.getName() + " is now spectating instance " + targetGameInstanceId);
        }
        // Else if target player is a game player, set him spectate his game
        else{
            for(GameInstance instance : gamesManager.getInstances()){
                if(instance.isPlayerOnInstance(new MmcPlayer(targetPlayer))){
                    gamesManager.getSpectators().replace(new MmcPlayer(player), instance.getInstanceId());
                    System.out.println("Player " + player.getName() + " is now spectating instance " + instance.getInstanceId());
                    return;
                }
            }
        }
    }

}
