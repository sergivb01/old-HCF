
package net.veilmc.hcf.timer.type;

import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.classes.PvpClass;
import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.TimerRunnable;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class PvpClassWarmupTimer
extends PlayerTimer
implements Listener {
    protected final ConcurrentMap<Object, Object> classWarmups;
    private final HCF plugin;

    public PvpClassWarmupTimer(HCF plugin) {
        super(ConfigurationService.PVP_CLASS_WARMUP_TIMER, TimeUnit.SECONDS.toMillis(10), false);
        this.plugin = plugin;
        this.classWarmups = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000, TimeUnit.MILLISECONDS).build().asMap();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, ()->{
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                PvpClassWarmupTimer.this.attemptEquip(player);
            }
        }, 20L, 20L);
        /*new BukkitRunnable(){
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    PvpClassWarmupTimer.this.attemptEquip(player);
                }
            }
        }.runTaskTimer(plugin, 10L , 10L);*/
    }

    @Override
    public void onDisable(Config config) {
        super.onDisable(config);
        this.classWarmups.clear();
    }

    @Override
    public ChatColor getScoreboardPrefix() {
        return ConfigurationService.PVP_CLASS_WARMUP_COLOUR;
    }

    @Override
    public TimerRunnable clearCooldown(UUID playerUUID) {
        TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.classWarmups.remove(playerUUID);
            return runnable;
        }
        return null;
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        String className = (String)this.classWarmups.remove(userUUID);
        if(this.classWarmups.remove(userUUID) == null){
            return;
        }
        //Preconditions.checkNotNull((Object)className, "Attempted to equip a class for %s, but nothing was added", (Object[])new Object[]{player.getName()});
        this.plugin.getPvpClassManager().setEquippedClass(player, this.plugin.getPvpClassManager().getPvpClass(className));
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerKick(PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onEquipmentSet(EquipmentSetEvent event) {
        HumanEntity humanEntity = event.getHumanEntity();
        if (humanEntity instanceof Player) {
            this.attemptEquip((Player)humanEntity);
        }
    }

    private void attemptEquip(Player player) {
        PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (equipped != null) {
            if (equipped.isApplicableFor(player)) {
                return;
            }
            this.plugin.getPvpClassManager().setEquippedClass(player, null);
        }
        PvpClass warmupClass = null;
        String warmup = (String)this.classWarmups.get(player.getUniqueId());
        if (warmup != null && !(warmupClass = this.plugin.getPvpClassManager().getPvpClass(warmup)).isApplicableFor(player)) {
            this.clearCooldown(player.getUniqueId());
        }
        Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
        for (PvpClass pvpClass : pvpClasses) {
            if (warmupClass == pvpClass || !pvpClass.isApplicableFor(player)) continue;
            this.classWarmups.put(player.getUniqueId(), pvpClass.getName());
            this.setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);
            break;
        }
    }

}

