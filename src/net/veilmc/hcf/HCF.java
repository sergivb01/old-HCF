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
import net.veilmc.hcf.command.*;
import net.veilmc.hcf.command.crate.KeyListener;
import net.veilmc.hcf.command.crate.KeyManager;
import net.veilmc.hcf.command.crate.LootExecutor;
import net.veilmc.hcf.command.death.DeathExecutor;
import net.veilmc.hcf.command.lives.LivesExecutor;
import net.veilmc.hcf.command.spawn.SpawnCommand;
import net.veilmc.hcf.command.spawn.TokenExecutor;
import net.veilmc.hcf.config.PotionLimiterData;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.hcf.deathban.DeathbanListener;
import net.veilmc.hcf.deathban.DeathbanManager;
import net.veilmc.hcf.deathban.FlatFileDeathbanManager;
import net.veilmc.hcf.faction.FactionExecutor;
import net.veilmc.hcf.faction.FactionManager;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.FlatFileFactionManager;
import net.veilmc.hcf.faction.claim.Claim;
import net.veilmc.hcf.faction.claim.ClaimHandler;
import net.veilmc.hcf.faction.claim.ClaimWandListener;
import net.veilmc.hcf.faction.claim.Subclaim;
import net.veilmc.hcf.faction.type.*;
import net.veilmc.hcf.kothgame.CaptureZone;
import net.veilmc.hcf.kothgame.EventExecutor;
import net.veilmc.hcf.kothgame.EventScheduler;
import net.veilmc.hcf.kothgame.conquest.ConquestExecutor;
import net.veilmc.hcf.kothgame.eotw.EOTWHandler;
import net.veilmc.hcf.kothgame.eotw.EotwCommand;
import net.veilmc.hcf.kothgame.eotw.EotwListener;
import net.veilmc.hcf.kothgame.faction.CapturableFaction;
import net.veilmc.hcf.kothgame.faction.ConquestFaction;
import net.veilmc.hcf.kothgame.faction.KothFaction;
import net.veilmc.hcf.kothgame.koth.KothExecutor;
import net.veilmc.hcf.listener.*;
import net.veilmc.hcf.listener.fixes.*;
import net.veilmc.hcf.scoreboard.ScoreboardHandler;
import net.veilmc.hcf.tab.TabListener;
import net.veilmc.hcf.timer.TimerExecutor;
import net.veilmc.hcf.timer.TimerManager;
import net.veilmc.hcf.timer.type.SotwTimer;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.user.UserManager;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.utils.Cooldowns;
import net.veilmc.hcf.utils.DateTimeFormats;
import net.veilmc.hcf.utils.Message;
import net.veilmc.hcf.visualise.ProtocolLibHook;
import net.veilmc.hcf.visualise.VisualiseHandler;
import net.veilmc.hcf.visualise.WallBorderListener;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Getter
public class HCF extends JavaPlugin{
	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");
	public static final long HOUR = TimeUnit.HOURS.toMillis(1);
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
	private static HCF plugin;
	public EventScheduler eventScheduler;
	public String scoreboardTitle;
	public long NEXT_KOTH = -1;
	public ArrayList<String> players;
	private Message message;
	private List<String> eventGames = new ArrayList<>();
	private Random random = new Random();
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
	private String armor;

	public static Permission permission = null;
	public static Chat chat = null;
	public static Economy econ = null;

	public static HCF getPlugin(){
		return plugin;
	}

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

	private void registerGames(){
		eventGames.addAll(getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction).map(Faction::getName).collect(Collectors.toList()));
		if(!(eventGames.isEmpty())){
			eventGames.forEach(System.out::print);
		}
	}

	@Override
	public void onEnable(){
		if(!setupChat()){
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		plugin = this;


		CustomEntityRegistration.registerCustomEntities();
		ProtocolLibHook.hook(this);

		this.saveDefaultConfig();

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		ConfigurationService.init(this.getConfig());
		PotionLimiterData.getInstance().setup(this);
		PotionLimitListener.reload();

		worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin && Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled() ? (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit") : null;

		registerConfiguration();
		registerCommands();
		registerManagers();
		registerListeners();

		Cooldowns.createCooldown("revive_cooldown");
		Cooldowns.createCooldown("Assassin_item_cooldown");
		Cooldowns.createCooldown("Archer_item_cooldown");
		Cooldowns.createCooldown("Archer_jump_cooldown");
		Cooldowns.createCooldown("report_cooldown");
		Cooldowns.createCooldown("helpop_cooldown");

		scoreboardTitle = ChatColor.translateAlternateColorCodes('&', getConfig().getString("scoreboard.title"));
		armor = ChatColor.translateAlternateColorCodes('&', getConfig().getString("scoreboard.active-class"));

		timerManager.enable();

		registerGames();

		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7----------------[&3*'&bOpulent&3'*]----------------"));
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7| - &bVersion: &f" + HCF.getPlugin().getDescription().getVersion()));
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7| - &bVault: &fHooked"));
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7------------------[&3*'&bHCF&3'*]------------------"));

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			List<String> donors = new ArrayList<>();
			Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("donor.broadcast") && !player.isOp() && !player.hasPermission("*")).forEach(player -> donors.add(player.getDisplayName()));

			HCF.getInstance().getConfig().getStringList("online-donors").forEach(s ->
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s
							.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT + "")
							.replace("%MEDICS%", donors.isEmpty() ? "&cNone" :
									donors.toString()
											.replace("[", "")
											.replace("]", "")))));

		}, 15 * 20L, (10 * 60) * 20L);

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
			Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Starting backup of data");
			Bukkit.getWorlds().forEach(world -> {
				world.setThundering(false);
				world.setStorm(false);
			});

			saveData();
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAutoSave &eTask was completed."));
		}, 10 * 20L, (60 * 15) * 20L);


		int seconds = (ConfigurationService.KIT_MAP ? 300 : 7200);
		startNewKoth(seconds);
		NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lKOTH &7» &eA new KOTH will be starting in&5 " + (ConfigurationService.KIT_MAP ? "5 minnutes" : "2 hours") + "!"));
	}

	public void startNewKoth(int seconds){
		if(eventGames.isEmpty()){
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[" + HCF.getPlugin().getDescription().getName() + "] &cCould not find any events to automatically start."));
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[" + HCF.getPlugin().getDescription().getName() + "] &cRegister KOTHs and restart server to enable this feature."));

		}else{
			getLogger().info("Starting koth in " + seconds + " seconds. (" + getNextGame() + ")");
			NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
			new BukkitRunnable(){
				@Override
				public void run(){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event start " + getNextGame());
					NEXT_KOTH = -1;
				}
			}.runTaskLater(this, 20L * seconds);
		}
	}

	public void rotateGames(){
		getLogger().info("Game list was rotated.");
		Collections.rotate(eventGames, -1);
	}

	public String getNextGame(){
		return eventGames.get(0);
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

	@Override
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
		PluginManager manager = getServer().getPluginManager();

		if(ConfigurationService.TAB){
			manager.registerEvents(new TabListener(this), this);
		}

		manager.registerEvents(new PotionLimitListener(), this);
		manager.registerEvents(new AutoRespawnListener(this), this);
		manager.registerEvents(new PortalFixListener(), this);
		manager.registerEvents(new ElevatorListener(this), this);
		manager.registerEvents(new EndPortalCommand(this), this);
		manager.registerEvents(new PermissionsCommand(this), this);
		manager.registerEvents(new ColonFix(), this);
		manager.registerEvents(new PotionListener(), this);
		manager.registerEvents(new PexCrashFix(), this);
		manager.registerEvents(new DupeGlitchFix(), this);
		manager.registerEvents(new DonorOnlyListener(), this);
		manager.registerEvents(new ArcherClass(this), this);
		manager.registerEvents(new KeyListener(this), this);
		manager.registerEvents(new WeatherFixListener(), this);
		manager.registerEvents(new EndermanFixListener(), this);
		manager.registerEvents(new MinecartElevatorListener(), this);
		manager.registerEvents(new StoreCommand(this), this);
		manager.registerEvents(new AutoSmeltOreListener(), this);
		manager.registerEvents(new BlockHitFixListener(), this);
		manager.registerEvents(new BlockJumpGlitchFixListener(), this);
		manager.registerEvents(new HungerFixListener(), this);
		manager.registerEvents(new BoatGlitchFixListener(), this);
		manager.registerEvents(new BookDeenchantListener(), this);
		manager.registerEvents(new BorderListener(), this);
		manager.registerEvents(new BottledExpListener(), this);
		manager.registerEvents(new ChatListener(this), this);
		manager.registerEvents(new ClaimWandListener(this), this);
		manager.registerEvents(new CombatLogListener(this), this);
		manager.registerEvents(new CoreListener(this), this);
		manager.registerEvents(new CreeperFriendlyListener(), this);
		manager.registerEvents(new CrowbarListener(this), this);
		manager.registerEvents(new DeathListener(this), this);
		manager.registerEvents(new DeathMessageListener(this), this);
		manager.registerEvents(new DeathSignListener(this), this);
		manager.registerEvents(new DeathbanListener(this), this);
		manager.registerEvents(new EnchantLimitListener(), this);
		manager.registerEvents(new EnderChestRemovalListener(), this);
		manager.registerEvents(new EntityLimitListener(), this);

		//TODO: Switch between flatfile & mongodb
		manager.registerEvents(new FlatFileFactionManager(this), this);

		manager.registerEvents(new EndListener(), this);
		manager.registerEvents(new EotwListener(this), this);
		manager.registerEvents(new EventSignListener(), this);
		manager.registerEvents(new ExpMultiplierListener(), this);
		manager.registerEvents(new EnchantSecurityListener(), this);
		manager.registerEvents(new FactionListener(this), this);
		manager.registerEvents(new HitDetectionListener(), this);
		manager.registerEvents(new FoundDiamondsListener(), this);
		manager.registerEvents(new FurnaceSmeltSpeederListener(this), this);
		manager.registerEvents(new InfinityArrowFixListener(), this);
		manager.registerEvents(new KitListener(this), this);
		manager.registerEvents(new ItemStatTrackingListener(), this);
		manager.registerEvents(new PearlGlitchListener(this), this);
		manager.registerEvents(new PotionLimitListener(), this);
		manager.registerEvents(new FactionsCoreListener(this), this);
		manager.registerEvents(new SignSubclaimListener(this), this);
		manager.registerEvents(new ShopSignListener(this), this);
		manager.registerEvents(new SkullListener(), this);
		manager.registerEvents(new BookQuillFixListener(), this);
		manager.registerEvents(new BeaconStrengthFixListener(), this);
		manager.registerEvents(new VoidGlitchFixListener(), this);
		manager.registerEvents(new WallBorderListener(this), this);
		manager.registerEvents(new WorldListener(this), this);
		manager.registerEvents(new UnRepairableListener(), this);
		manager.registerEvents(new SotwListener(this), this);
		//manager.registerEvents(new StatTrackListener(), this);
		manager.registerEvents(new CobbleCommand(), this);
	}


	private void registerCommands(){
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
		getCommand("cobble").setExecutor(new CobbleCommand());
		getCommand("ores").setExecutor(new OresCommand());
		getCommand("crowgive").setExecutor(new CrowbarGiveCommand());
		getCommand("user").setExecutor(new UserCommand(this));
		final Map<String, Map<String, Object>> map = getDescription().getCommands();

		for(final Map.Entry<String, Map<String, Object>> entry : map.entrySet()){
			final PluginCommand command = getCommand(entry.getKey());
			command.setPermission("hcf.command." + entry.getKey());
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

	public String getKothRemaining(){
		long duration = NEXT_KOTH - System.currentTimeMillis();
		return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
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