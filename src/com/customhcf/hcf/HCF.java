package com.customhcf.hcf;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.ServerHandler;
import com.customhcf.hcf.Utils.*;
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
import com.customhcf.hcf.fixes.*;
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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
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
        return org.apache.commons.lang.time.DurationFormatUtils.formatDuration((long) duration, (String) ((duration >= HOUR ? "HH:" : "") + "mm:ss"));
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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "savedata");
            saveData();
        }, 0L, (60 * 20) * 20L);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Setup save task");

        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "clearlag 100000");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Set clearlag delay");
    }

    private void saveData() {
        this.deathbanManager.saveDeathbanData();
        this.economyManager.saveEconomyData();
        this.factionManager.saveFactionData();
        this.userManager.saveUserData();
        this.keyManager.saveKeyData();
        this.deathbanManager.saveDeathbanData();
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
        ConfigurationSerialization.registerClass((Class)CaptureZone.class);
        ConfigurationSerialization.registerClass((Class)Deathban.class);
        ConfigurationSerialization.registerClass((Class)Claim.class);
        ConfigurationSerialization.registerClass((Class)Subclaim.class);
        ConfigurationSerialization.registerClass((Class)Deathban.class);
        ConfigurationSerialization.registerClass((Class)FactionUser.class);
        ConfigurationSerialization.registerClass((Class)ClaimableFaction.class);
        ConfigurationSerialization.registerClass((Class)ConquestFaction.class);
        ConfigurationSerialization.registerClass((Class)CapturableFaction.class);
        ConfigurationSerialization.registerClass((Class)KothFaction.class);
        ConfigurationSerialization.registerClass((Class)EndPortalFaction.class);
        ConfigurationSerialization.registerClass((Class)Faction.class);
        ConfigurationSerialization.registerClass((Class)FactionMember.class);
        ConfigurationSerialization.registerClass((Class)PlayerFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.class);
        ConfigurationSerialization.registerClass((Class)SpawnFaction.class);
        ConfigurationSerialization.registerClass((Class)GlowstoneFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.WestRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)GlowstoneFaction.class);

    }

    private void registerListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents((Listener)new FactionManageArgument(this), (Plugin)this);
        manager.registerEvents((Listener)new ElevatorListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EndPortalCommand(this), (Plugin)this);
        manager.registerEvents((Listener)new ColonFix(), (Plugin)this);
        manager.registerEvents((Listener)new PotionListener(), (Plugin)this);
        manager.registerEvents((Listener)new PexCrashFix(), (Plugin)this);
        manager.registerEvents((Listener)new DupeGlitchFix(), (Plugin)this);
        manager.registerEvents((Listener)new DonorOnlyListener(), (Plugin)this);
        manager.registerEvents((Listener)new ArcherClass(this), (Plugin)this);
        manager.registerEvents((Listener)new KeyListener(this), (Plugin)this);
        manager.registerEvents((Listener)new WeatherFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new MinecartElevatorListener(), (Plugin)this);
        manager.registerEvents(new StoreCommand(this), (Plugin)this);
        //manager.registerEvents((Listener)new NoPermissionClickListener(), (Plugin)this);
        manager.registerEvents((Listener)new AutoSmeltOreListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockHitFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockJumpGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new HungerFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BoatGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BookDeenchantListener(), (Plugin)this);
        manager.registerEvents((Listener)new BorderListener(), (Plugin)this);
        manager.registerEvents((Listener)new BottledExpListener(), (Plugin)this);
        manager.registerEvents((Listener)new ChatListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ClaimWandListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CombatLogListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CoreListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CreeperFriendlyListener(), (Plugin)this);
        manager.registerEvents((Listener)new CrowbarListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathMessageListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathSignListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathbanListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EnchantLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new EnderChestRemovalListener(), (Plugin)this);
        manager.registerEvents((Listener)new EntityLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new FlatFileFactionManager(this), (Plugin)this);
        manager.registerEvents((Listener)new EndListener(), (Plugin)this);
        manager.registerEvents((Listener)new EotwListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EventSignListener(), (Plugin)this);
        manager.registerEvents((Listener)new ExpMultiplierListener(), (Plugin)this);
        manager.registerEvents((Listener)new FactionListener(this), (Plugin)this);
        manager.registerEvents((Listener)new HitDetectionListener(), (Plugin)this);
        this.foundDiamondsListener = new FoundDiamondsListener(this);
        manager.registerEvents((Listener)this.foundDiamondsListener, (Plugin)this);
        manager.registerEvents((Listener)new FurnaceSmeltSpeederListener(this), (Plugin)this);
        manager.registerEvents((Listener)new InfinityArrowFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new KitListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ItemStatTrackingListener(), (Plugin)this);
        manager.registerEvents((Listener)new PearlGlitchListener(this), (Plugin)this);
        manager.registerEvents((Listener)new PotionLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new FactionsCoreListener(this), (Plugin)this);
        manager.registerEvents((Listener)new SignSubclaimListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ShopSignListener(this), (Plugin)this);
        manager.registerEvents((Listener)new SkullListener(), (Plugin)this);
        manager.registerEvents((Listener)new BeaconStrengthFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new VoidGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new WallBorderListener(this), (Plugin)this);
        manager.registerEvents((Listener)new WorldListener(this), (Plugin)this);
        manager.registerEvents((Listener)new UnRepairableListener(), (Plugin)this);
        manager.registerEvents(new SotwListener(this), (Plugin)this);
        //manager.registerEvents(new StatTrackListener(), this);
        manager.registerEvents(new CobbleCommand(), this);

    }

    private void registerCommands() {
        this.getCommand("ffa").setExecutor((CommandExecutor)new FFACommand());
        this.getCommand("endportal").setExecutor((CommandExecutor)new EndPortalCommand(this));
        this.getCommand("toggleend").setExecutor((CommandExecutor)new ToggleEnd(this));
        this.getCommand("focus").setExecutor(new FactionFocusArgument(this));
        this.getCommand("spawner").setExecutor((CommandExecutor)new SpawnerCommand(this));
        this.getCommand("spawndragon").setExecutor((CommandExecutor)new EndDragonCommand(this));
        this.getCommand("sotw").setExecutor(new SotwCommand(this));
        this.getCommand("blacklist").setExecutor(new BlacklistCommand(this));
        this.getCommand("dinfo").setExecutor(new DInfoCommand(this));
        this.getCommand("conquest").setExecutor((CommandExecutor)new ConquestExecutor(this));
        this.getCommand("crowbar").setExecutor((CommandExecutor)new CrowbarCommand());
        this.getCommand("economy").setExecutor((CommandExecutor)new EconomyCommand(this));
        this.getCommand("eotw").setExecutor((CommandExecutor)new EotwCommand(this));
        this.getCommand("game").setExecutor((CommandExecutor)new EventExecutor(this));
        this.getCommand("help").setExecutor((CommandExecutor)new HelpCommand());
        this.getCommand("faction").setExecutor((CommandExecutor)new FactionExecutor(this));
        this.getCommand("gopple").setExecutor((CommandExecutor)new GoppleCommand(this));
        this.getCommand("stats").setExecutor((CommandExecutor)new PlayerStats());
        this.getCommand("koth").setExecutor((CommandExecutor)new KothExecutor(this));
        this.getCommand("store").setExecutor((CommandExecutor)new StoreCommand(this));
        this.getCommand("lives").setExecutor((CommandExecutor)new LivesExecutor(this));
        this.getCommand("location").setExecutor((CommandExecutor)new LocationCommand(this));
        this.getCommand("logout").setExecutor((CommandExecutor)new LogoutCommand(this));
        this.getCommand("mapkit").setExecutor((CommandExecutor)new com.customhcf.hcf.command.MapKitCommand(this));
        this.getCommand("pay").setExecutor((CommandExecutor)new PayCommand(this));
        this.getCommand("pvptimer").setExecutor((CommandExecutor)new PvpTimerCommand(this));
        this.getCommand("refund").setExecutor((CommandExecutor)new RefundCommand());
        this.getCommand("coords").setExecutor(new coords(this));
        this.getCommand("servertime").setExecutor((CommandExecutor)new ServerTimeCommand());
        this.getCommand("spawn").setExecutor((CommandExecutor)new SpawnCommand(this));
        this.getCommand("timer").setExecutor((CommandExecutor)new TimerExecutor(this));
        this.getCommand("revive").setExecutor(new ReviveCommand(this));
        this.getCommand("savedata").setExecutor(new SaveDataCommand());
        this.getCommand("staffinfo").setExecutor(new StaffScript());
        this.getCommand("setborder").setExecutor((CommandExecutor)new SetBorderCommand());
        this.getCommand("loot").setExecutor((CommandExecutor)new LootExecutor(this));
        this.getCommand("safestop").setExecutor(new SafestopCommand());
        this.getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
        //this.getCommand("icons").setExecutor(new IconsCommand());
        this.getCommand("cobble").setExecutor(new CobbleCommand());
        this.getCommand("ores").setExecutor(new OresCommand());
        this.getCommand("crowgive").setExecutor(new CrowbarGiveCommand());
        final Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>)this.getDescription().getCommands();
        for (final Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final PluginCommand command = this.getCommand((String)entry.getKey());
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