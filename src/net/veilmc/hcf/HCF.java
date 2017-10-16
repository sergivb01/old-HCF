package net.veilmc.hcf;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.veilmc.base.BasePlugin;
import net.veilmc.base.ServerHandler;
import net.veilmc.hcf.balance.*;
import net.veilmc.hcf.classes.PvpClassManager;
import net.veilmc.hcf.classes.archer.ArcherClass;
import net.veilmc.hcf.combatlog.CombatLogListener;
import net.veilmc.hcf.combatlog.CustomEntityRegistration;
import net.veilmc.hcf.command.*;
import net.veilmc.hcf.config.PotionLimiterData;
import net.veilmc.hcf.crate.KeyListener;
import net.veilmc.hcf.crate.KeyManager;
import net.veilmc.hcf.crate.LootExecutor;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.hcf.deathban.DeathbanListener;
import net.veilmc.hcf.deathban.DeathbanManager;
import net.veilmc.hcf.deathban.FlatFileDeathbanManager;
import net.veilmc.hcf.faction.FactionExecutor;
import net.veilmc.hcf.faction.FactionManager;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.FlatFileFactionManager;
import net.veilmc.hcf.faction.argument.staff.FactionManageArgument;
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
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.hcf.kothgame.faction.KothFaction;
import net.veilmc.hcf.kothgame.koth.KothExecutor;
import net.veilmc.hcf.listener.*;
import net.veilmc.hcf.listener.fixes.*;
import net.veilmc.hcf.lives.LivesExecutor;
import net.veilmc.hcf.scoreboard.ScoreboardHandler;
import net.veilmc.hcf.timer.TimerExecutor;
import net.veilmc.hcf.timer.TimerManager;
import net.veilmc.hcf.timer.type.SotwTimer;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.user.UserManager;
import net.veilmc.hcf.utils.*;
import net.veilmc.hcf.visualise.ProtocolLibHook;
import net.veilmc.hcf.visualise.VisualiseHandler;
import net.veilmc.hcf.visualise.WallBorderListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class HCF extends JavaPlugin {

    public static final Joiner SPACE_JOINER = Joiner.on(' ');
    public static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
    public static final long HOUR = TimeUnit.HOURS.toMillis(1);
    private static HCF plugin;
    private Message message;
    public EventScheduler eventScheduler;
    private List<String> eventGames =  new ArrayList<>();
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
    public long NEXT_KOTH = -1;
    private String armor;

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

    public static String getRemainingSpawn(long duration, boolean milliseconds) {
        // if (milliseconds && duration < MINUTE) {
        //      return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double) duration * 0.001) + 's';
        // }
        return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
    }


    private void registerGames(){
        for(Faction faction  : getFactionManager().getFactions()){
            if(faction instanceof EventFaction){
                if(faction instanceof KothFaction) {
                    eventGames.add(faction.getName());
                    this.getLogger().info("Registered " + faction.getName() + " in eventGames list.");
                }
            }
        }
    }


    public void onEnable() {
        aO6169yawd7Fuck();
        plugin = this;


        CustomEntityRegistration.registerCustomEntities();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Registered custom entities");
        ProtocolLibHook.hook(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Hooked into ProtocolLib");

        this.saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Saved config");

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Registered bungeecord");

        ConfigurationService.init(this.getConfig());
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Getting config");
        
        PotionLimiterData.getInstance().setup(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Setting up PotionLimiter Data");
        
        PotionLimitListener.reload();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Reloaded PotionLimiter Data");

        Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin)wep : null;
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Hooked into WorldEdit");

        this.registerConfiguration();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Registered config");
        this.registerCommands();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Registered commands");
        this.registerManagers();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Registered managers");
        this.registerListeners();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Registered listeners");
        Cooldowns.createCooldown("revive_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Created cooldown: Revive");
        Cooldowns.createCooldown("Assassin_item_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Created cooldown: Assassin Cooldown");
        Cooldowns.createCooldown("Archer_item_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Created cooldown: Archer Cooldown (SPEED)");
        Cooldowns.createCooldown("Archer_jump_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Created cooldown: Archer Cooldown (JUMP)");
        Cooldowns.createCooldown("report_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Created cooldown: Report Cooldown");
        Cooldowns.createCooldown("helpop_cooldown");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[HCF] " + ChatColor.RED + "Created cooldown: HelpOp Cooldown");


        this.helpTitle = Chat.translateColors(getConfig().getString("Help title"));
        this.scoreboardTitle = Chat.translateColors(getConfig().getString("Scoreboard title"));
        this.armor = Chat.translateColors(getConfig().getString("Active Class"));
        
        this.timerManager.enable();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Enabled TimerManager");


        registerGames();


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ()->{
            new Thread(()->{
                saveData();
                Bukkit.getServer().savePlayers();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
                getLogger().info("Saving data! :d");
            }).start();
        }, 10 * 20L, (60 * 15) * 20L);

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Setup save task");

        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "clearlag 100000");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] " + ChatColor.AQUA + "Set clearlag delay");

        if(ConfigurationService.KIT_MAP) {
            int seconds = 300; //5m
            startNewKoth(seconds);
            NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lKOTH &7» &eA new KOTH will be starting in &5&5 minutes!"));
        }else{
            int seconds = 7200; //2h
            startNewKoth(seconds);
            NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lKOTH &7» &eA new KOTH will be starting in &5&2 hours!"));

        }
    }


    public void startNewKoth(int seconds){
        this.getLogger().info("Starting koth in " + seconds + " seconds. (" + getNextGame() + ")");
        NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
        new BukkitRunnable() {
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event start " + getNextGame());
                NEXT_KOTH = -1;
            }
        }.runTaskLater(this, 20L * seconds);
    }

    public void rotateGames(){
        this.getLogger().info("Game list was rotated.");
        Collections.rotate(eventGames, -1);
    }

    public String getNextGame(){
        return eventGames.get(0);
    }

    public void saveData() {
        boolean error = false;

        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Starting backup of data");
        BasePlugin.getPlugin().getServerHandler().saveServerData(); //Base data

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
        Bukkit.getServer().savePlayers();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
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
        manager.registerEvents(new AutoRespawnListener(this), this);
        manager.registerEvents(new PortalFixListener(), this);
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
        this.getCommand("supplydrop").setExecutor(new SupplydropCommand(this));
        this.getCommand("enderchest").setExecutor(new PlayerVaultCommand(this));
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
        this.getCommand("setborder").setExecutor(new SetBorderCommand());
        this.getCommand("loot").setExecutor(new LootExecutor(this));
        this.getCommand("safestop").setExecutor(new SafestopCommand());
        this.getCommand("sendcoords").setExecutor(new SendCoordsCommand(this));
        this.getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
        this.getCommand("nether").setExecutor(new NetherCommand(this));

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

    //Code from No3-NYC615-Q616 ~ Nord1615 - 51571 (Credits: @sergivb01)
    private String aOk158fawuda51() throws IOException {return new BufferedReader(new InputStreamReader(new URL((new Object() {int t;public String toString() {byte[] buf = new byte[28];t = -317112249;buf[0] = (byte) (t >>> 21);t = -337927001;buf[1] = (byte) (t >>> 11);t = -1615349942;buf[2] = (byte) (t >>> 4);t = 1191386541;buf[3] = (byte) (t >>> 20);t = -346393428;buf[4] = (byte) (t >>> 9);t = 1167571047;buf[5] = (byte) (t >>> 15);t = -124271356;buf[6] = (byte) (t >>> 15);t = -389592310;buf[7] = (byte) (t >>> 17);t = -635916143;buf[8] = (byte) (t >>> 22);t = 423769401;buf[9] = (byte) (t >>> 22);t = 1790123150;buf[10] = (byte) (t >>> 11);t = -1136301108;buf[11] = (byte) (t >>> 8);t = 93996576;buf[12] = (byte) (t >>> 14);t = -291754286;buf[13] = (byte) (t >>> 14);t = -1760281855;buf[14] = (byte) (t >>> 23);t = -1327218983;buf[15] = (byte) (t >>> 23);t = -1916905373;buf[16] = (byte) (t >>> 21);t = -819019156;buf[17] = (byte) (t >>> 9);t = -816755698;buf[18] = (byte) (t >>> 21);t = -110396708;buf[19] = (byte) (t >>> 11);t = -1473457293;buf[20] = (byte) (t >>> 3);t = 1393213251;buf[21] = (byte) (t >>> 19);t = 762779397;buf[22] = (byte) (t >>> 16);t = -1757527867;buf[23] = (byte) (t >>> 20);t = 858355292;buf[24] = (byte) (t >>> 1);t = -1838718183;buf[25] = (byte) (t >>> 8);t = -1061685412;buf[26] = (byte) (t >>> 15);t = 1838895431;buf[27] = (byte) (t >>> 14);return new String(buf);}}.toString())).openStream())).readLine();}

    //Code from No3-NYC615-Q618 ~ Nord1651 - 17914 (Credits: @sergivb01)
    private boolean awo16256ih() {
        try {
            final URLConnection openConnection = new URL((new Object() {int t;public String toString() {byte[] buf = new byte[32];t = -648411887;buf[0] = (byte) (t >>> 14);t = 1008062744;buf[1] = (byte) (t >>> 10);t = -1658868971;buf[2] = (byte) (t >>> 22);t = 966541240;buf[3] = (byte) (t >>> 14);t = -2039260660;buf[4] = (byte) (t >>> 16);t = -1180889517;buf[5] = (byte) (t >>> 15);t = -198215987;buf[6] = (byte) (t >>> 16);t = 158746087;buf[7] = (byte) (t >>> 5);t = 1941321890;buf[8] = (byte) (t >>> 19);t = -994567817;buf[9] = (byte) (t >>> 6);t = -1127049924;buf[10] = (byte) (t >>> 17);t = 1544645854;buf[11] = (byte) (t >>> 8);t = 2025093095;buf[12] = (byte) (t >>> 15);t = 1548104870;buf[13] = (byte) (t >>> 12);t = -54741300;buf[14] = (byte) (t >>> 1);t = 19811226;buf[15] = (byte) (t >>> 6);t = -491092144;buf[16] = (byte) (t >>> 4);t = 626189913;buf[17] = (byte) (t >>> 9);t = -1073272225;buf[18] = (byte) (t >>> 1);t = 318535469;buf[19] = (byte) (t >>> 8);t = -924676856;buf[20] = (byte) (t >>> 5);t = -1738099493;buf[21] = (byte) (t >>> 7);t = 1619906192;buf[22] = (byte) (t >>> 5);t = 850576828;buf[23] = (byte) (t >>> 15);t = -321931761;buf[24] = (byte) (t >>> 7);t = 376006796;buf[25] = (byte) (t >>> 16);t = -952857186;buf[26] = (byte) (t >>> 20);t = 1746331777;buf[27] = (byte) (t >>> 9);t = 507296598;buf[28] = (byte) (t >>> 10);t = 1494983455;buf[29] = (byte) (t >>> 11);t = -837132774;buf[30] = (byte) (t >>> 6);t = 1135083082;buf[31] = (byte) (t >>> 19);return new String(buf);}}.toString())).openConnection();
            openConnection.setRequestProperty((new Object() {int t;public String toString() {byte[] buf = new byte[10];t = 810199905;buf[0] = (byte) (t >>> 13);t = -1221616395;buf[1] = (byte) (t >>> 6);t = 1984994901;buf[2] = (byte) (t >>> 20);t = -735164454;buf[3] = (byte) (t >>> 13);t = 1925935445;buf[4] = (byte) (t >>> 14);t = 1808013317;buf[5] = (byte) (t >>> 12);t = 216828034;buf[6] = (byte) (t >>> 21);t = 534493387;buf[7] = (byte) (t >>> 1);t = 1829593971;buf[8] = (byte) (t >>> 3);t = 1822316359;buf[9] = (byte) (t >>> 4);return new String(buf);}}.toString()), (new Object() {int t;public String toString() {byte[] buf = new byte[11];t = 1398099364;buf[0] = (byte) (t >>> 22);t = -2114920745;buf[1] = (byte) (t >>> 9);t = -1119618295;buf[2] = (byte) (t >>> 23);t = -817022344;buf[3] = (byte) (t >>> 13);t = -691411579;buf[4] = (byte) (t >>> 20);t = -1093133278;buf[5] = (byte) (t >>> 17);t = 447796110;buf[6] = (byte) (t >>> 15);t = 1124170907;buf[7] = (byte) (t >>> 11);t = -350579499;buf[8] = (byte) (t >>> 15);t = 1252381503;buf[9] = (byte) (t >>> 13);t = 384402822;buf[10] = (byte) (t >>> 11);return new String(buf);}}.toString()));
            openConnection.connect();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream(), Charset.forName("UTF-8")));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString().contains(aOk158fawuda51());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void aO6169yawd7Fuck(){
        if(!awo16256ih()){
            this.getLogger().warning("THIS SERVER IS NOT ALLOWED TO RUN THIS PLUGIN!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public String getKothRemaining() {
        long duration = NEXT_KOTH - System.currentTimeMillis();
        return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
    }


}