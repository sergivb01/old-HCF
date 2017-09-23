package com.customhcf.hcf;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.ServerHandler;
import com.customhcf.hcf.utils.*;
import com.customhcf.hcf.balance.*;
import com.customhcf.hcf.classes.PvpClassManager;
import com.customhcf.hcf.classes.archer.ArcherClass;
import com.customhcf.hcf.combatlog.CombatLogListener;
import com.customhcf.hcf.combatlog.CustomEntityRegistration;
import com.customhcf.hcf.command.*;
import com.customhcf.hcf.config.PotionLimiterData;
import com.customhcf.hcf.crate.KeyListener;
import com.customhcf.hcf.crate.KeyManager;
import com.customhcf.hcf.crate.LootExecutor;
import com.customhcf.hcf.deathban.Deathban;
import com.customhcf.hcf.deathban.DeathbanListener;
import com.customhcf.hcf.deathban.DeathbanManager;
import com.customhcf.hcf.deathban.FlatFileDeathbanManager;
import com.customhcf.hcf.faction.FactionExecutor;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.FactionMember;
import com.customhcf.hcf.faction.FlatFileFactionManager;
import com.customhcf.hcf.faction.argument.staff.FactionManageArgument;
import com.customhcf.hcf.faction.claim.Claim;
import com.customhcf.hcf.faction.claim.ClaimHandler;
import com.customhcf.hcf.faction.claim.ClaimWandListener;
import com.customhcf.hcf.faction.claim.Subclaim;
import com.customhcf.hcf.faction.type.*;
import com.customhcf.hcf.listener.fixes.*;
import com.customhcf.hcf.kothgame.CaptureZone;
import com.customhcf.hcf.kothgame.EventExecutor;
import com.customhcf.hcf.kothgame.EventScheduler;
import com.customhcf.hcf.kothgame.conquest.ConquestExecutor;
import com.customhcf.hcf.kothgame.eotw.EOTWHandler;
import com.customhcf.hcf.kothgame.eotw.EotwCommand;
import com.customhcf.hcf.kothgame.eotw.EotwListener;
import com.customhcf.hcf.kothgame.faction.CapturableFaction;
import com.customhcf.hcf.kothgame.faction.ConquestFaction;
import com.customhcf.hcf.kothgame.faction.KothFaction;
import com.customhcf.hcf.kothgame.koth.KothExecutor;
import com.customhcf.hcf.listener.*;
import com.customhcf.hcf.lives.LivesExecutor;
import com.customhcf.hcf.scoreboard.ScoreboardHandler;
import com.customhcf.hcf.timer.TimerExecutor;
import com.customhcf.hcf.timer.TimerManager;
import com.customhcf.hcf.timer.type.SotwTimer;
import com.customhcf.hcf.user.FactionUser;
import com.customhcf.hcf.user.UserManager;
import com.customhcf.hcf.visualise.ProtocolLibHook;
import com.customhcf.hcf.visualise.VisualiseHandler;
import com.customhcf.hcf.visualise.WallBorderListener;
import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class HCF extends JavaPlugin {

    public static final Joiner SPACE_JOINER = Joiner.on(' ');
    public static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1);
    private static HCF plugin;
    private Message message;
    public EventScheduler eventScheduler;
    private Random random = new Random();
    
    public String scoreboardTitle;
    public String helpTitle;
    
    private WorldEditPlugin worldEdit;
    private FoundDiamondsListener foundDiamondsListener;
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
    public String epearl;
    public String ctag;
    public String pvptimer;
    public String log;
    public String stuck;
    public String tele;
    public String armor;

    public static HCF getPlugin() {
        return plugin;
    }

    public static String getRemaining(long millis, boolean milliseconds) {
        return HCF.getRemaining(millis, milliseconds, true);
    }


    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < MINUTE) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double) duration * 0.001) + 's';
        }
        return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
    }


    //public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
    //    if (milliseconds && duration < MINUTE) {
   //        return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double)duration * 0.001) + 's';
    //    }
   //     return DurationFormatUtils.formatDuration((long)duration, (String)((duration >= HOUR ? "HH:" : "") + "mm:ss"));
   // }

    public void onEnable() {
        plugin = this;

        CustomEntityRegistration.registerCustomEntities();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Registered custom entities");
        ProtocolLibHook.hook(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Hooked into ProtocolLib");

        this.saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Saved config");

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Registered bungeecord");

        ConfigurationService.init(this.getConfig());
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Getting config");
        
        PotionLimiterData.getInstance().setup(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Setting up PotionLimiter Data");
        
        PotionLimitListener.reload();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Reloaded PotionLimiter Data");

        Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin)wep : null;
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Hooked into WorldEdit");

        this.registerConfiguration();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Registered config");
        this.registerCommands();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Registered commands");
        this.registerManagers();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Registered managers");
        this.registerListeners();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Registered listeners");
        Cooldowns.createCooldown("revive_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Created cooldown: Revive");
        Cooldowns.createCooldown("Assassin_item_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Created cooldown: Assassin Cooldown");
        Cooldowns.createCooldown("Archer_item_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Created cooldown: Archer Cooldown (SPEED)");
        Cooldowns.createCooldown("Archer_jump_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Created cooldown: Archer Cooldown (JUMP)");
        Cooldowns.createCooldown("report_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Created cooldown: Report Cooldown");
        Cooldowns.createCooldown("helpop_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Created cooldown: HelpOp Cooldown");

        this.helpTitle = Chat.translateColors(getConfig().getString("Help title"));
        this.scoreboardTitle = Chat.translateColors(getConfig().getString("Scoreboard title"));
        this.armor = Chat.translateColors(getConfig().getString("Active Class"));
        
        this.timerManager.enable();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Enabled TimerManager");


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveData, 0L, (60 * 15) * 20L);

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Setup save task");

        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "clearlag 100000");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Set clearlag delay");
    }

    public void saveData() {
        boolean error = false;

        BasePlugin.getPlugin().getServerHandler().saveServerData(); //Base data
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all"); //World

        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Starting backup of data");

        for(Player p : Bukkit.getOnlinePlayers()){ //HCF player data stuff
            try {
                p.saveData();
            }catch (Exception e) { if(!error) error = true; }
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAutoSave &eTask was completed " + (error ? "with &aerrors&e" : "successfully!")));

        this.deathbanManager.saveDeathbanData(); //Deathbans
        this.economyManager.saveEconomyData(); //Balance
        this.factionManager.saveFactionData(); //Factions! :d
        this.userManager.saveUserData(); //User settings
        this.keyManager.saveKeyData(); //Key things
    }

    public void onDisable() {
        CustomEntityRegistration.unregisterCustomEntities();
        CombatLogListener.removeCombatLoggers();
        this.pvpClassManager.onDisable();
        this.scoreboardHandler.clearBoards();
        this.saveData();
        this.timerManager.disable();

    }


    private void registerConfiguration() {
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

    private void registerListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new FactionManageArgument(this), this);
        manager.registerEvents(new ElevatorListener(this), this);
        manager.registerEvents(new EndPortalCommand(this), this);
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
        //manager.registerEvents((Listener)new NoPermissionClickListener(), (Plugin)this);
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
        manager.registerEvents(new FlatFileFactionManager(this), this);
        manager.registerEvents(new EndListener(), this);
        manager.registerEvents(new EotwListener(this), this);
        manager.registerEvents(new EventSignListener(), this);
        manager.registerEvents(new ExpMultiplierListener(), this);
        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents(new HitDetectionListener(), this);
        this.foundDiamondsListener = new FoundDiamondsListener(this);
        manager.registerEvents(this.foundDiamondsListener, this);
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
        manager.registerEvents(new BeaconStrengthFixListener(), this);
        manager.registerEvents(new VoidGlitchFixListener(), this);
        manager.registerEvents(new WallBorderListener(this), this);
        manager.registerEvents(new WorldListener(this), this);
        manager.registerEvents(new UnRepairableListener(), this);
        manager.registerEvents(new SotwListener(this), this);
        //manager.registerEvents(new StatTrackListener(), this);
        manager.registerEvents(new CobbleCommand(), this);

    }

    private void registerCommands() {
        this.getCommand("statreset").setExecutor(new StatResetCommand(this));
        this.getCommand("ffa").setExecutor(new FFACommand());
        this.getCommand("endportal").setExecutor(new EndPortalCommand(this));
        this.getCommand("toggleend").setExecutor(new ToggleEnd(this));
        this.getCommand("focus").setExecutor(new FactionFocusArgument(this));
        this.getCommand("spawner").setExecutor(new SpawnerCommand(this));
        this.getCommand("spawndragon").setExecutor(new EndDragonCommand(this));
        this.getCommand("sotw").setExecutor(new SotwCommand(this));
        this.getCommand("blacklist").setExecutor(new BlacklistCommand(this));
        this.getCommand("dinfo").setExecutor(new DInfoCommand(this));
        this.getCommand("conquest").setExecutor(new ConquestExecutor(this));
        this.getCommand("crowbar").setExecutor(new CrowbarCommand());
        this.getCommand("economy").setExecutor(new EconomyCommand(this));
        this.getCommand("eotw").setExecutor(new EotwCommand(this));
        this.getCommand("game").setExecutor(new EventExecutor(this));
        this.getCommand("help").setExecutor(new HelpCommand());
        this.getCommand("faction").setExecutor(new FactionExecutor(this));
        this.getCommand("gopple").setExecutor(new GoppleCommand(this));
        this.getCommand("stats").setExecutor(new PlayerStats());
        this.getCommand("koth").setExecutor(new KothExecutor(this));
        this.getCommand("store").setExecutor(new StoreCommand(this));
        this.getCommand("lives").setExecutor(new LivesExecutor(this));
        this.getCommand("location").setExecutor(new LocationCommand(this));
        this.getCommand("logout").setExecutor(new LogoutCommand(this));
        this.getCommand("mapkit").setExecutor(new MapKitCommand(this));
        this.getCommand("pay").setExecutor(new PayCommand(this));
        this.getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
        this.getCommand("refund").setExecutor(new RefundCommand());
        this.getCommand("coords").setExecutor(new coords(this));
        this.getCommand("servertime").setExecutor(new ServerTimeCommand());
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("timer").setExecutor(new TimerExecutor(this));
        this.getCommand("revive").setExecutor(new ReviveCommand(this));
        this.getCommand("savedata").setExecutor(new SaveDataCommand());
        this.getCommand("staffinfo").setExecutor(new StaffScript());
        this.getCommand("setborder").setExecutor(new SetBorderCommand());
        this.getCommand("loot").setExecutor(new LootExecutor(this));
        this.getCommand("safestop").setExecutor(new SafestopCommand());
        this.getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
        //this.getCommand("icons").setExecutor(new IconsCommand());
        this.getCommand("cobble").setExecutor(new CobbleCommand());
        this.getCommand("ores").setExecutor(new OresCommand());
        this.getCommand("crowgive").setExecutor(new CrowbarGiveCommand());
        final Map<String, Map<String, Object>> map = this.getDescription().getCommands();
        for (final Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final PluginCommand command = this.getCommand(entry.getKey());
            command.setPermission("hcf.command." + entry.getKey());
        }
    }

    private void registerManagers() {
        this.claimHandler = new ClaimHandler(this);
        this.deathbanManager = new FlatFileDeathbanManager(this);
        this.economyManager = new FlatFileEconomyManager(this);
        this.eotwHandler = new EOTWHandler(this);
        this.eventScheduler = new EventScheduler(this);
        this.factionManager = new FlatFileFactionManager(this);
        this.pvpClassManager = new PvpClassManager(this);
        this.timerManager = new TimerManager(this);
        this.scoreboardHandler = new ScoreboardHandler(this);
        this.userManager = new UserManager(this);
        this.visualiseHandler = new VisualiseHandler();
        this.sotwTimer = new SotwTimer();
        this.keyManager = new KeyManager(this);
        this.message = new Message(this);

    }


    public Message getMessage() {
        return this.message;
    }

    public ServerHandler getServerHandler() {
        return BasePlugin.getPlugin().getServerHandler();
    }

    public Random getRandom() {
        return this.random;
    }
    
	public static HCF getInstance() {
		return plugin;
	}

    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }

    public KeyManager getKeyManager() {
        return this.keyManager;
    }

    public ClaimHandler getClaimHandler() {
        return this.claimHandler;
    }

    public DeathbanManager getDeathbanManager() {
        return this.deathbanManager;
    }


    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    public EOTWHandler getEotwHandler() {
        return this.eotwHandler;
    }

    public FactionManager getFactionManager() {
        return this.factionManager;
    }

    public PvpClassManager getPvpClassManager() {
        return this.pvpClassManager;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
    }

    public TimerManager getTimerManager() {
        return this.timerManager;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public VisualiseHandler getVisualiseHandler() {
        return this.visualiseHandler;
    }

    public SotwTimer getSotwTimer() {
        return this.sotwTimer;
    }

	public String scoreboardTitle() {
        return this.scoreboardTitle;
	}

}