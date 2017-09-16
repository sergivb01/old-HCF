
package com.customhcf.hcf.listener;

import com.customhcf.base.kit.event.KitApplyEvent;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.timer.Timer;
import com.customhcf.hcf.timer.TimerManager;
import com.customhcf.hcf.timer.TimerRunnable;
import com.customhcf.hcf.timer.event.TimerStartEvent;
import com.customhcf.hcf.timer.type.PvpProtectionTimer;
import com.google.common.base.Optional;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitMapListener
implements Listener {
    final HCF plugin;

    public KitMapListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onTimer(TimerStartEvent e) {
        if (e.getTimer() instanceof PvpProtectionTimer) {
            this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown((UUID)e.getUserUUID().get());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(e.getPlayer()) >= 0) {
            this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onKitApplyMonitor(KitApplyEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

}

