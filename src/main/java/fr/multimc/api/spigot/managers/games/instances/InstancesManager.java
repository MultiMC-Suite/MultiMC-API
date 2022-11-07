package fr.multimc.api.spigot.managers.games.instances;

import fr.multimc.api.commons.tools.times.MmcTime;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.tools.chat.TextBuilder;
import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import fr.multimc.api.spigot.tools.utils.MessageType;
import fr.multimc.api.spigot.tools.utils.MessagesFactory;
import fr.multimc.api.spigot.tools.worlds.MmcWorld;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class InstancesManager implements Listener {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final MessagesFactory factory;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;
    private final MmcWorld lobbyWorld;
    private final MmcWorld gameWorld;

    private final List<Instance> instances = new ArrayList<>();
    private final HashMap<Integer, InstanceState> instancesState = new HashMap<>();
    private List<MmcTeam> mmcTeams = new ArrayList<>();

    private boolean isStarted = false;
    private int allocations = 0;

    /**
     * Default constructor for the InstancesManager
     * @param plugin JavaPlugin instance
     * @param instanceClass Class used for the instances
     * @param settings Instance settings
     * @param lobbyWorld MmcWorld instance that represent the Lobby world
     * @param gameWorld MmcWorld instance that represent the Game world
     */
    public InstancesManager(@NotNull JavaPlugin plugin,
                            @NotNull Class<? extends Instance> instanceClass,
                            @NotNull InstanceSettings settings,
                            @Nullable MessagesFactory factory,
                            @NotNull MmcWorld lobbyWorld,
                            @NotNull MmcWorld gameWorld) {
        this.plugin = plugin;
        this.instanceClass = instanceClass;
        this.settings = settings;
        this.factory = factory;
        this.logger = plugin.getLogger();
        this.lobbyWorld = lobbyWorld;
        this.gameWorld = gameWorld;
        this.gameWorld.getWorldSettings().setGameMode(null); // Disable game mode changing for game world
        // Register local events handlers
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Pre-allocate schematics for instances
     * @param allocateCount Number of instances to allocate
     */
    public void preAllocate(int allocateCount){
        this.logger.info(String.format("Allocating schematics for %d instances", allocateCount));
        for(int i = 0; i < allocateCount; i++){
            this.logger.info(String.format("Allocating for instance %d/%d", i + 1, allocateCount));
            Location location = new Location(this.gameWorld.getWorld(), i * 1024, 100, 0);
            Instance.allocate(this.settings.schematic(), this.settings.schematicOptions(), location);
        }
        this.allocations = allocateCount;
        this.logger.info(String.format("%d slots allocated", allocateCount));
    }

    /**
     * Start the instances manager from sync to async
     * @param mmcTeams List of MmcTeam instances
     */
    public void start(@NotNull List<MmcTeam> mmcTeams){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                startAsync(mmcTeams);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Start the instances manager asynchronously
     * @param mmcTeams List of MmcTeam instances
     */
    private void startAsync(@NotNull List<MmcTeam> mmcTeams) throws InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        this.instances.clear();
        this.instancesState.clear();
        this.mmcTeams = new ArrayList<>(mmcTeams);
        List<List<MmcTeam>> gameTeams = new ArrayList<>();
        switch (this.settings.gameType()) {
            case SOLO -> {
                List<MmcTeam> onePlayerTeams = this.getOnePlayerTeams();
                for(MmcTeam onePlayerTeam : onePlayerTeams){
                    List<MmcTeam> tempTeamList = new ArrayList<>();
                    tempTeamList.add(onePlayerTeam);
                    gameTeams.add(tempTeamList);
                }
            }
            case ONLY_TEAM -> {
                List<MmcTeam> localTeams2 = new ArrayList<>(mmcTeams);
                for(MmcTeam team: localTeams2){
                    List<MmcTeam> tempTeamList = new ArrayList<>();
                    tempTeamList.add(team);
                    gameTeams.add(tempTeamList);
                }
            }
            case TEAM_VS_TEAM -> gameTeams = this.getTeamsTuple(this.mmcTeams);
        }
        // Create instances
        for(int i = 0; i < gameTeams.size(); i++){
            this.logger.info(String.format("Creating instance %d/%d", i + 1, gameTeams.size()));
            Location location = new Location(this.gameWorld.getWorld(), i * 1024, 100, 0);
            this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, gameTeams.get(i)));
        }
        if(this.instances.size() == 0){
            this.logger.warning("No instance created");
            return;
        }
        // Init instances
        this.initInstances();
        // Start instances
        this.startInstances();
        this.isStarted = true;
        this.allocations = 0; // Reset allocated slots to reset maps
    }

    /**
     * Init all instances
     */
    private void initInstances() throws InterruptedException {
        long dt;
        long dtAvg = 0;
        for(int i = 0; i < this.instances.size(); i++){
            // Runnable
            int finalI = i;
            long finalDtAvg = dtAvg;
            int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () ->
                    this.sendTeamActionBar(new TextBuilder(String.format("&bInstance &6%d/%d &binitialized (&e%s&3 remaining)",
                            finalI + 1,
                            this.instances.size(),
                            MmcTime.format(finalDtAvg * (this.instances.size() - finalI - 1), "mm:ss"))).build()), 0L, 20L); // 20 ticks = 1 second
            // Instance loading
            dt = this.initInstance(this.instances.get(i), this.instances.get(i).getInstanceId());
            if(dtAvg == 0){
                dtAvg = dt;
            }
            dtAvg = (dtAvg + dt) / 2;
            Bukkit.getScheduler().cancelTask(taskID);
        }
        for(MmcPlayer player: this.getSpectators()){
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.teleport(this.gameWorld.getSpawnPoint());
                player.setGameMode(GameMode.SPECTATOR);
            });
        }
        for(int i = 5; i >= 0; i--){
            Thread.sleep(1000);
            if (i != 0) this.sendTeamTitle(new TextBuilder(String.format("&6%d", i)).build(), new TextBuilder("&7Get ready...").build());
            else this.sendTeamTitle(new TextBuilder("&fLet's &bGO!").build(), null);
            this.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
    }

    /**
     * Start all instances
     */
    private void startInstances(){
        this.instances.forEach(this::startInstance);
    }

    /**
     * Stop all instances
     */
    private void stopInstances(){
        instances.forEach(this::stopInstance);
    }

    /**
     * Init an instance
     * @param instance Instance to init
     * @param instanceId ID of the instance
     * @return Time taken to init the instance
     */
    private long initInstance(@NotNull Instance instance, int instanceId) {
        long dt = System.currentTimeMillis();
        instance.init(instanceId < this.allocations);
        return System.currentTimeMillis() - dt;
    }

    /**
     * Start an instance
     * @param instance Instance to start
     */
    private void startInstance(@NotNull Instance instance){
        instance.start();
    }

    /**
     * Stop an instance
     * @param instance Instance to stop
     */
    private void stopInstance(@NotNull Instance instance){
       instance.stop();
    }

    /**
     * Called by instance to update their state, and if all are stopped, reset the instances manager
     * @param instanceId ID if the instance
     * @param state New state for the instance
     */
    protected void updateInstanceState(int instanceId, @NotNull InstanceState state){
        if(instancesState.containsKey(instanceId)){
            this.logger.info(String.format("Instance %d update state from %s to %s", instanceId, instancesState.get(instanceId), state));
            instancesState.replace(instanceId, state);
        } else {
            this.logger.info(String.format("Instance %d set state to %s", instanceId, state));
            instancesState.put(instanceId, state);
        }
        // Check if all instances are stopped
        boolean isAllStopped = true;
        for(int _instanceId: this.instancesState.keySet()){
            if(this.instancesState.get(_instanceId) != InstanceState.STOP){
                isAllStopped = false;
                break;
            }
        }
        if(isAllStopped){
            this.stopManager();
        }
    }

    /**
     * Reset the instances manager
     */
    public void stopManager(){
        this.stopInstances();
        for(MmcPlayer player: this.getSpectators()){
            player.teleportSync(this.plugin, this.lobbyWorld.getSpawnPoint(), false);
        }
        for(MmcTeam mmcTeam: this.mmcTeams){
            mmcTeam.getPlayers().forEach(mmcPlayer -> mmcPlayer.teleportSync(this.plugin, this.lobbyWorld.getSpawnPoint(), false));
        }
        this.isStarted = false;
    }

    /**
     * Send message to all teams
     * @param message Message to send
     */
    private void broadcast(@NotNull Component message) {
        for(MmcTeam mmcTeam : this.mmcTeams) mmcTeam.sendMessage(message);
    }

    /**
     * Send title to all teams
     * @param title Title to send
     * @param subtitle Subtitle to send
     */
    private void sendTeamTitle(@Nullable Component title, @Nullable Component subtitle) {
        for(MmcTeam mmcTeam : this.mmcTeams) mmcTeam.sendTitle(title, subtitle);
    }

    /**
     * Send action bar to all teams
     * @param actionBar Action bar to send
     */
    private void sendTeamActionBar(@NotNull Component actionBar) {
        for(MmcTeam mmcTeam : this.mmcTeams) mmcTeam.sendActionBar(actionBar);
    }

    /**
     * Play sound to all teams
     * @param sound Sound to play
     */
    @SuppressWarnings("SameParameterValue")
    private void playSound(@NotNull Sound sound) {
        for(MmcTeam mmcTeam : this.mmcTeams){
            mmcTeam.playSound(sound);
        }
    }

    /**
     * Get teams of 1 players (for solo game type)
     * @return List of teams with only 1 player into
     */
    private List<MmcTeam> getOnePlayerTeams(){
        List<MmcTeam> mmcTeams = new ArrayList<>();
        for(MmcTeam mmcTeam : this.mmcTeams){
            for(MmcPlayer player: mmcTeam.getPlayers()){
                MmcTeam onePlayerMmcTeam = new MmcTeam(mmcTeam.getName(), mmcTeam.getTeamCode(), player);
                mmcTeams.add(onePlayerMmcTeam);
            }
        }
        return mmcTeams;
    }

    /**
     * For Team VS Team game type, get tuple of teams
     * @param mmcTeams List of teams
     * @return List of tuple of teams
     */
    private List<List<MmcTeam>> getTeamsTuple(@NotNull List<MmcTeam> mmcTeams) {
        List<List<MmcTeam>> teamsTuple = new ArrayList<>();
        if(mmcTeams.size() % 2 == 0){
            for(int i = 0; i < mmcTeams.size(); i += 2){
                List<MmcTeam> mmcTeamTuple = new ArrayList<>();
                mmcTeamTuple.add(mmcTeams.get(i));
                mmcTeamTuple.add(mmcTeams.get(i+1));
                teamsTuple.add(mmcTeamTuple);
            }
        }
        return teamsTuple;
    }

    /**
     * Get the list of spectators
     * @return List of spectators
     */
    private List<MmcPlayer> getSpectators() {
        List<MmcPlayer> nonPlayerPlayers = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            MmcPlayer mmcPlayer = new MmcPlayer(player);
            boolean isPlayer = false;
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(mmcPlayer)){
                    isPlayer = true;
                    break;
                }
            }
            if(!isPlayer){
                nonPlayerPlayers.add(mmcPlayer);
            }
        }
        return nonPlayerPlayers;
    }

    /**
     * Get instance count from the game type and the number of teams/players
     * @return Instance count to create
     */
    private int getInstanceCount(){
        switch (this.settings.gameType()){
            case SOLO:
                int count = 0;
                for(MmcTeam mmcTeam : this.mmcTeams){
                    count += mmcTeam.getTeamSize();
                }
                return count;
            case ONLY_TEAM:
                return this.mmcTeams.size();
            case TEAM_VS_TEAM:
                if(this.mmcTeams.size() % 2 == 0) {
                    return this.mmcTeams.size() / 2;
                }
            default:
                return -1;
        }
    }

    /**
     * Get a team from a MmcPlayer object
     * @param mmcPlayer MmcPlayer object
     * @return Team of the player
     */
    @Nullable
    private MmcTeam getTeamFromPlayer(@NotNull MmcPlayer mmcPlayer){
        for(MmcTeam mmcTeam: this.mmcTeams){
            if(mmcTeam.getPlayers().contains(mmcPlayer)) return mmcTeam;
        }
        return null;
    }

    /**
     * Get an instance from a MmcPlayer object
     * @param mmcPlayer MmcPlayer object
     * @return Instance of the player
     */
    @Nullable
    private Instance getInstanceFromPlayer(@NotNull MmcPlayer mmcPlayer){
        for(Instance instance: this.instances){
            if(instance.getPlayers().contains(mmcPlayer)) return instance;
        }
        return null;
    }

    public boolean isSpectator(@Nullable MmcPlayer player){
        return this.getSpectators().contains(player);
    }
    public MmcWorld getLobbyWorld(){
        return this.lobbyWorld;
    }
    public MmcWorld getGameWorld(){
        return this.gameWorld;
    }
    public boolean isStarted() {
        return this.isStarted;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(e.getPlayer().getWorld().equals(this.gameWorld.getWorld())){
            if(!this.isStarted){
                if(e.getFrom().distance(e.getTo()) > 0){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        if(!this.isStarted){
            this.logger.info(String.format("Instance manager not started, teleporting player %s to lobby", mmcPlayer.getName()));
            mmcPlayer.teleport(this.getLobbyWorld().getSpawnPoint());
            player.getInventory().clear();
            this.logger.info(String.format("Player %s teleported to lobby...", mmcPlayer.getName()));
            return;
        }
        for(Instance instance : this.instances){
            if(!instance.isPlayerOnInstance(mmcPlayer)) continue;
            if(instance.isRunning()){
                this.logger.info(String.format("Reconnecting player %s to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
                instance.onPlayerReconnect(mmcPlayer);
                this.logger.info(String.format("Player %s reconnected to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
            }else{
                this.logger.info(String.format("Instance %d not running, teleporting player %s to lobby...", instance.getInstanceId(), mmcPlayer.getName()));
                mmcPlayer.teleport(this.getLobbyWorld().getSpawnPoint());
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
        if(!this.isStarted) return;
        for(Instance instance : this.instances){
            if(!instance.isPlayerOnInstance(mmcPlayer) || !instance.isRunning()) continue;
            this.logger.info(String.format("Disconnecting player %s to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
            instance.onPlayerDisconnect(mmcPlayer);
            this.logger.info(String.format("Player %s disconnected to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getWorld().equals(this.gameWorld.getWorld())){
            if(!this.isStarted){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(e.getEntity().getWorld().equals(this.gameWorld.getWorld())){
            if(!this.isStarted){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e){
        if(this.factory == null) return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        Component newMessage;
        if(!this.isStarted){
            newMessage = this.factory.getMessage(MessageType.PREFIXED, Component.text(player.getName()), e.message(), null);
            for(Player _player: Bukkit.getOnlinePlayers()){
                _player.sendMessage(newMessage);
            }
            return;
        }
        for(Instance instance: this.instances){
            if(!instance.isPlayerOnInstance(new MmcPlayer(player))) continue;
            MmcTeam team = this.getTeamFromPlayer(mmcPlayer);
            switch (this.settings.gameType()) {
                case SOLO, ONLY_TEAM -> {
                    // Send message to player's team
                    if (team == null) return;
                    newMessage = this.factory.getMessage(MessageType.TEAM, Component.text(player.getName()), e.message(), Component.text(team.getName()));
                    team.sendMessage(newMessage);
                }
                case TEAM_VS_TEAM -> {
                    String rawMessage = PlainTextComponentSerializer.plainText().serialize(e.message());
                    String chatPrefix = MessageType.GAME.getChatPrefix();
                    if (chatPrefix == null || team == null) return;
                    if (rawMessage.startsWith(chatPrefix)) {
                        // Send message to instance players
                        String message = rawMessage.replaceFirst(chatPrefix, "");
                        newMessage = this.factory.getMessage(MessageType.GAME, Component.text(player.getName()), new TextBuilder(message).build(), Component.text(team.getName()));
                        for (MmcTeam instanceTeam : instance.getTeams()) {
                            instanceTeam.sendMessage(newMessage);
                        }
                    } else {
                        // Send message to player's team
                        newMessage = this.factory.getMessage(MessageType.TEAM, Component.text(player.getName()), e.message(), Component.text(team.getName()));
                        team.sendMessage(newMessage);
                    }
                }
                default -> player.sendMessage(Component.text("Game type not supported").color(NamedTextColor.RED));
            }
            return;
        }
    }
}
