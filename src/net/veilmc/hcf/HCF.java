package net.veilmc.hcf;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.veilmc.base.BasePlugin;
import net.veilmc.hcf.balance.*;
import net.veilmc.hcf.classes.PvpClassManager;
import net.veilmc.hcf.classes.archer.ArcherClass;
import net.veilmc.hcf.combatlog.CombatLogListener;
import net.veilmc.hcf.combatlog.CustomEntityRegistration;
import net.veilmc.hcf.commands.*;
import net.veilmc.hcf.commands.crate.KeyListener;
import net.veilmc.hcf.commands.crate.KeyManager;
import net.veilmc.hcf.commands.crate.LootExecutor;
import net.veilmc.hcf.commands.death.DeathExecutor;
import net.veilmc.hcf.commands.lives.LivesExecutor;
import net.veilmc.hcf.commands.spawn.SpawnCommand;
import net.veilmc.hcf.commands.spawn.TokenExecutor;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.hcf.deathban.DeathbanListener;
import net.veilmc.hcf.deathban.DeathbanManager;
import net.veilmc.hcf.deathban.FlatFileDeathbanManager;
import net.veilmc.hcf.events.CaptureZone;
import net.veilmc.hcf.events.EventExecutor;
import net.veilmc.hcf.events.EventScheduler;
import net.veilmc.hcf.events.conquest.ConquestExecutor;
import net.veilmc.hcf.events.eotw.EOTWHandler;
import net.veilmc.hcf.events.eotw.EotwCommand;
import net.veilmc.hcf.events.eotw.EotwListener;
import net.veilmc.hcf.events.faction.CapturableFaction;
import net.veilmc.hcf.events.faction.ConquestFaction;
import net.veilmc.hcf.events.faction.KothFaction;
import net.veilmc.hcf.events.koth.KothExecutor;
import net.veilmc.hcf.faction.FactionExecutor;
import net.veilmc.hcf.faction.FactionManager;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.FlatFileFactionManager;
import net.veilmc.hcf.faction.claim.Claim;
import net.veilmc.hcf.faction.claim.ClaimHandler;
import net.veilmc.hcf.faction.claim.ClaimWandListener;
import net.veilmc.hcf.faction.claim.Subclaim;
import net.veilmc.hcf.faction.type.*;
import net.veilmc.hcf.listeners.*;
import net.veilmc.hcf.listeners.fixes.*;
import net.veilmc.hcf.scoreboard.ScoreboardHandler;
import net.veilmc.hcf.tab.PlayerTab;
import net.veilmc.hcf.timer.TimerExecutor;
import net.veilmc.hcf.timer.TimerManager;
import net.veilmc.hcf.timer.type.SotwTimer;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.user.UserManager;
import net.veilmc.hcf.utils.ClientAPI;
import net.veilmc.hcf.utils.Cooldowns;
import net.veilmc.hcf.utils.DateTimeFormats;
import net.veilmc.hcf.utils.Message;
import net.veilmc.hcf.utils.config.ConfigurationService;
import net.veilmc.hcf.utils.config.PotionLimiterData;
import net.veilmc.hcf.utils.runnables.AutoSaveRunnable;
import net.veilmc.hcf.utils.runnables.DonorBroadcastRunnable;
import net.veilmc.hcf.visualise.ProtocolLibHook;
import net.veilmc.hcf.visualise.VisualiseHandler;
import net.veilmc.hcf.visualise.WallBorderListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Getter
public class HCF extends JavaPlugin implements PluginMessageListener{
	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");
	public static final long HOUR = TimeUnit.HOURS.toMillis(1);
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
	public static Permission permission = null;
	public static Chat chat = null;
	public static Economy econ = null;
	private static HCF plugin;
	private Message message;
	private WorldEditPlugin worldEdit;
	private ClaimHandler claimHandler;
	private SotwTimer sotwTimer;
	private KeyManager keyManager;
	private DeathbanManager deathbanManager;
	private EconomyManager economyManager;
	private EOTWHandler eotwHandler;
	private FactionManager factionManager;
	private PvpClassManager pvpClassManager;
	private ScoreboardHandler scoreboardHandler;
	private TimerManager timerManager;
	private UserManager userManager;
	private VisualiseHandler visualiseHandler;
	private EventScheduler eventScheduler;
	public static List<UUID> cbUser;

	public static String getRemaining(long millis, boolean milliseconds){
		return HCF.getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail){
		if(milliseconds && duration < MINUTE){
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double) duration * 0.001) + 's';
		}
		return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
	}

	public static HCF getInstance(){
		return plugin;
	}

	public static HCF getPlugin(){
		return plugin;
	}

	@Override
	public void onEnable(){
		if(!setupChat()){
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		plugin = this;

		cbUser = new ArrayList<>();
		registerClientCheck();

		CustomEntityRegistration.registerCustomEntities();
		ProtocolLibHook.hook(this);

		this.saveDefaultConfig();

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		ConfigurationService.init(this.getConfig());
		PotionLimiterData.getInstance().setup(this);
		PotionLimitListener.reload();

		worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin && Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled() ? (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit") : null;
		eventScheduler = new EventScheduler(this);

		registerConfiguration();
		registerCommands();
		registerManagers();
		registerListeners();

		Cooldowns.createCooldown("revive_cooldown");
		Cooldowns.createCooldown("Assassin_item_cooldown");
		Cooldowns.createCooldown("Archer_item_cooldown");
		Cooldowns.createCooldown("Archer_jump_cooldown");

		timerManager.enable();

		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7----------------[&3*'&bOpulent&3'*]----------------"));
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7| - &bVersion: &f" + HCF.getPlugin().getDescription().getVersion()));
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7| - &bVault: &fHooked"));
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7------------------[&3*'&bHCF&3'*]------------------"));

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new DonorBroadcastRunnable(), 20L, 600 * 20L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new AutoSaveRunnable(), 20L, 900 * 20L);

	}

	public void saveData(){
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		BasePlugin.getPlugin().getServerHandler().saveServerData(); //Base data
		Bukkit.getOnlinePlayers().forEach(Player::saveData);//Save data
		deathbanManager.saveDeathbanData(); //Deathbans
		economyManager.saveEconomyData(); //Balance
		factionManager.saveFactionData(); //Factions
		userManager.saveUserData(); //User settings
		keyManager.saveKeyData(); //Key things
	}

	public void onDisable(){
		Bukkit.getServer().savePlayers();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		CustomEntityRegistration.unregisterCustomEntities();
		CombatLogListener.removeCombatLoggers();
		pvpClassManager.onDisable();
		scoreboardHandler.clearBoards();
		saveData();
		timerManager.disable();
	}

	private void registerClientCheck() {
		Bukkit.getPluginManager().registerEvents(new ClientAPI(), this);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "CB|INIT", this::onPluginMessageReceived);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "CB-Binary", this::onPluginMessageReceived);
	}

	private void registerConfiguration(){
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Subclaim.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(FactionUser.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(ConquestFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(GlowstoneFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
		ConfigurationSerialization.registerClass(GlowstoneFaction.class);
	}

	private void registerListeners(){
		if(ConfigurationService.TAB){
			new PlayerTab(this);
		}

		new CheatBreakerListener(this);
		new PotionLimitListener(this);
		new AutoRespawnListener(this);
		new PortalFixListener(this);
		new ElevatorListener(this);
		new EndPortalCommand(this);
		new PermissionsCommand(this);
		new ColonFix(this);
		new PotionListener(this);
		new PexCrashFix(this);
		new DupeGlitchFix(this);
		new DonorOnlyListener(this);
		new ArcherClass(this);
		new KeyListener(this);
		new WeatherFixListener(this);
		new EndermanFixListener(this);
		new MinecartElevatorListener(this);
		new StoreCommand(this);
		new AutoSmeltOreListener(this);
		new BlockHitFixListener(this);
		new BlockJumpGlitchFixListener(this);
		new HungerFixListener(this);
		new BoatGlitchFixListener(this);
		new BookDeenchantListener(this);
		new BorderListener(this);
		new BottledExpListener(this);
		new ChatListener(this);
		new ClaimWandListener(this);
		new CombatLogListener(this);
		new CoreListener(this);
		new CreeperFriendlyListener(this);
		new CrowbarListener(this);
		new DeathListener(this);
		new DeathMessageListener(this);
		new DeathSignListener(this);
		new DeathbanListener(this);
		new EnchantLimitListener(this);
		new EnderChestRemovalListener(this);
		new EntityLimitListener(this);

		//TODO: Switch between flatfile & mongodb
		new FlatFileFactionManager(this);
		new EndListener(this);
		new EotwListener(this);
		new EventSignListener(this);
		new ExpMultiplierListener(this);
		new EnchantSecurityListener(this);
		new FactionListener(this);
		new HitDetectionListener(this);
		new FoundDiamondsListener(this);
		new FurnaceSmeltSpeederListener(this);
		new InfinityArrowFixListener(this);
		new KitListener(this);
		new ItemStatTrackingListener(this);
		new PearlGlitchListener(this);
		new PotionLimitListener(this);
		new FactionsCoreListener(this);
		new SignSubclaimListener(this);
		new ShopSignListener(this);
		new SkullListener(this);
		new BookQuillFixListener(this);
		new BeaconStrengthFixListener(this);
		new VoidGlitchFixListener(this);
		new WallBorderListener(this);
		new WorldListener(this);
		new UnRepairableListener(this);
		new SotwListener(this);
		//new StatTrackListener(this);
	}

	private void registerCommands(){
		getCommand("test").setExecutor(new TestCommand());
		getCommand("permissions").setExecutor(new PermissionsCommand(this));
		getCommand("reclaim").setExecutor(new ReclaimCommand(this));
		getCommand("platinum").setExecutor(new PlatinumReviveCommand(this));
		getCommand("teamspeak").setExecutor(new TeamspeakCommand());
		getCommand("supplydrop").setExecutor(new SupplydropCommand(this));
		getCommand("enderchest").setExecutor(new PlayerVaultCommand(this));
		getCommand("statreset").setExecutor(new StatResetCommand(this));
		getCommand("togglefd").setExecutor(new TogglefdCommand());
		getCommand("ffa").setExecutor(new FFACommand());
		getCommand("endportal").setExecutor(new EndPortalCommand(this));
		getCommand("toggleend").setExecutor(new ToggleEnd(this));
		getCommand("focus").setExecutor(new FactionFocusArgument(this));
		getCommand("sendcoords").setExecutor(new SendCoordsCommand(this));
		getCommand("spawner").setExecutor(new SpawnerCommand(this));
		getCommand("sotw").setExecutor(new SotwCommand(this));
		getCommand("conquest").setExecutor(new ConquestExecutor(this));
		getCommand("crowbar").setExecutor(new CrowbarCommand());
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("eotw").setExecutor(new EotwCommand(this));
		getCommand("game").setExecutor(new EventExecutor(this));
		getCommand("help").setExecutor(new HelpCommand());
		getCommand("faction").setExecutor(new FactionExecutor(this));
		getCommand("gopple").setExecutor(new GoppleCommand(this));
		getCommand("stats").setExecutor(new PlayerStats());
		getCommand("koth").setExecutor(new KothExecutor(this));
		getCommand("check").setExecutor(new CheckCommand(this));
		getCommand("store").setExecutor(new StoreCommand(this));
		getCommand("lives").setExecutor(new LivesExecutor(this));
		getCommand("token").setExecutor(new TokenExecutor(this));
		getCommand("death").setExecutor(new DeathExecutor(this));
		getCommand("location").setExecutor(new LocationCommand(this));
		getCommand("logout").setExecutor(new LogoutCommand(this));
		getCommand("mapkit").setExecutor(new MapKitCommand(this));
		getCommand("pay").setExecutor(new PayCommand(this));
		getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
		getCommand("coords").setExecutor(new CoordsCommand(this));
		getCommand("servertime").setExecutor(new ServerTimeCommand());
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("timer").setExecutor(new TimerExecutor(this));
		getCommand("medic").setExecutor(new ReviveCommand(this));
		getCommand("savedata").setExecutor(new SaveDataCommand());
		getCommand("setborder").setExecutor(new SetBorderCommand());
		getCommand("loot").setExecutor(new LootExecutor(this));
		getCommand("safestop").setExecutor(new SafestopCommand());
		getCommand("nether").setExecutor(new NetherCommand(this));
		getCommand("cobble").setExecutor(new CobbleCommand(this));
		getCommand("ores").setExecutor(new OresCommand());
		getCommand("crowgive").setExecutor(new CrowbarGiveCommand());
		getCommand("user").setExecutor(new UserCommand(this));
		final Map<String, Map<String, Object>> map = getDescription().getCommands();

		for(final Map.Entry<String, Map<String, Object>> entry : map.entrySet()){
			final PluginCommand command = getCommand(entry.getKey());
			command.setPermission("hcf.commands." + entry.getKey());
		}

	}

	private void registerManagers(){
		claimHandler = new ClaimHandler(this);
		deathbanManager = new FlatFileDeathbanManager(this);
		economyManager = new FlatFileEconomyManager(this);
		eotwHandler = new EOTWHandler(this);
		factionManager = new FlatFileFactionManager(this);
		pvpClassManager = new PvpClassManager(this);
		timerManager = new TimerManager(this);
		scoreboardHandler = new ScoreboardHandler(this);
		userManager = new UserManager(this);
		visualiseHandler = new VisualiseHandler();
		sotwTimer = new SotwTimer();
		keyManager = new KeyManager(this);
		message = new Message(this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] arg2) {
		boolean cb = false;
		if (channel.equals("CB|INIT") || channel.equals("CB-Binary")) {
			cb = true;
		}
		if (!cbUser.contains(player.getUniqueId())) {
			if (cb) {
				cbUser.add(player.getUniqueId());
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GREEN + "Cheatbreaker has been detected!");
				player.sendMessage(ChatColor.GRAY + "Staff will be notified of this if required.");
				player.sendMessage(" ");
			}
		}
	}


	private boolean setupChat(){
		if(getServer().getPluginManager().getPlugin("Vault") == null){
			getLogger().severe("DB: Vault plugin = null");
			return false;
		}
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		if(rsp == null){
			getLogger().severe("rsp = null");
			return false;
		}
		chat = rsp.getProvider();
		return chat != null;
	}

}